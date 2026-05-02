package net.nwtech.qtty.adapters.out.persistence;

import lombok.RequiredArgsConstructor;
import net.nwtech.qtty.application.port.out.MovieRepositoryPort;
import net.nwtech.qtty.domain.model.MovieModel;
import net.nwtech.qtty.domain.model.MovieStatus;
import net.nwtech.qtty.domain.model.RatingRecordModel;
import net.nwtech.qtty.domain.model.UserModel;
import net.nwtech.qtty.entity.Movie;
import net.nwtech.qtty.entity.RatingRecord;
import net.nwtech.qtty.entity.User;
import net.nwtech.qtty.repositories.MovieRepository;
import net.nwtech.qtty.repositories.UserRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class MoviePersistenceAdapter implements MovieRepositoryPort {

    private final MovieRepository movieRepository;
    private final UserRepository userRepository;

    @Override
    public Optional<MovieModel> findById(long id) {
        return movieRepository.findByIdWithRatings(id).map(this::toDomain);
    }

    @Override
    public Optional<MovieModel> findByTmdbId(long tmdbId) {
        return movieRepository.findByTmdbIdWithRatings(tmdbId).map(this::toDomain);
    }

    @Override
    public Optional<MovieModel> findByTitle(String title) {
        return movieRepository.findByTitleWithRatings(title).map(this::toDomain);
    }

    @Override
    @Transactional
    public MovieModel save(MovieModel movie) {
        Movie entity = resolveEntity(movie);
        entity.setTmdbId(movie.tmdbId() != null ? movie.tmdbId() : entity.getTmdbId());
        entity.setDiscordRatingMessageId(movie.discordRatingMessageId() != null
                ? movie.discordRatingMessageId()
                : entity.getDiscordRatingMessageId());
        entity.setStatus(resolveStatus(entity.getStatus(), movie.status()));
        entity.setTitle(movie.title());
        entity.setSynopsis(movie.synopsis());
        entity.setImagePath(movie.imagePath() != null && !movie.imagePath().isBlank()
                ? movie.imagePath()
                : entity.getImagePath());
        entity.setReleaseDate(movie.releaseDate());

        Movie saved = movieRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    @Transactional
    public MovieModel updateRating(MovieModel movie, List<RatingRecordModel> ratingRecords) {
        Movie entity = movieRepository.findById(movie.id())
                .orElseThrow(() -> new IllegalArgumentException("Movie not found: " + movie.id()));

        Map<Long, RatingRecord> existingByUserId = entity.getRatings().stream()
                .filter(rating -> rating.getUser() != null && rating.getUser().getId() != null)
                .collect(Collectors.toMap(rating -> rating.getUser().getId(), Function.identity()));

        for (RatingRecordModel ratingModel : ratingRecords) {
            if (ratingModel == null || ratingModel.user() == null || ratingModel.user().id() == null) {
                throw new IllegalArgumentException("Rating must reference an existing user");
            }

            RatingRecord existingRating = existingByUserId.get(ratingModel.user().id());
            if (existingRating != null) {
                existingRating.setRating(ratingModel.rating());
                continue;
            }

            User user = userRepository.getReferenceById(ratingModel.user().id());
            RatingRecord newRating = new RatingRecord();
            newRating.setUser(user);
            newRating.setRating(ratingModel.rating());
            entity.addRating(newRating);
        }

        Movie saved = movieRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public List<MovieModel> saveAll(List<MovieModel> movies) {
        List<MovieModel> saved = new  ArrayList<>();
        for (MovieModel movie : movies) {
            saved.add(this.save(movie));
        }
        return saved;
    }

    private MovieModel toDomain(Movie entity) {
        if (entity == null) return null;

        return new  MovieModel(
                entity.getId(),
                entity.getTmdbId(),
                entity.getDiscordRatingMessageId(),
                entity.getStatus(),
                entity.getTitle(),
                entity.getSynopsis(),
                entity.getImagePath(),
                entity.getReleaseDate(),
                entity.getRatings() == null
                        ? new ArrayList<>()
                        : entity.getRatings().stream()
                        .map(this::mapRatingRecordToDomain)
                        .collect(Collectors.toCollection(ArrayList::new))
        );
    }

    private RatingRecordModel mapRatingRecordToDomain(RatingRecord entity) {
        return new RatingRecordModel(
                entity.getId(),
                mapUserModel(entity.getUser()),
                entity.getRating(),
                null
        );
    }

    private UserModel mapUserModel(User entity){
        if (entity == null) return null;
        return new UserModel(entity.getId(), entity.getDiscordId(), entity.getUsername());
    }

    @Transactional
    public MovieModel updateMovieMessageId(Long movieId, Long messageId){
        var movie = findById(movieId);
        if (movie.isEmpty()) {
            throw new IllegalArgumentException("Movie not found: " + movieId);
        }
        var movieEntity = movie.get();
        MovieModel updated = MovieModel
                .builder()
                .id(movieEntity.id())
                .tmdbId(movieEntity.tmdbId())
                .discordRatingMessageId(messageId)
                .status(MovieStatus.PUBLISHED)
                .title(movieEntity.title())
                .synopsis(movieEntity.synopsis())
                .imagePath(movieEntity.imagePath())
                .releaseDate(movieEntity.releaseDate())
                .ratings(movieEntity.ratings())
                .build();
        return save(updated);
    }

    private Movie resolveEntity(MovieModel movie) {
        if (movie.id() != null) {
            return movieRepository.findById(movie.id()).orElseGet(Movie::new);
        }
        if (movie.tmdbId() != null) {
            return movieRepository.findByTmdbId(movie.tmdbId()).orElseGet(Movie::new);
        }
        return new Movie();
    }

    private MovieStatus resolveStatus(MovieStatus currentStatus, MovieStatus requestedStatus) {
        if (currentStatus == MovieStatus.PUBLISHED && requestedStatus == MovieStatus.SEARCHED) {
            return MovieStatus.PUBLISHED;
        }
        if (requestedStatus != null) {
            return requestedStatus;
        }
        return currentStatus != null ? currentStatus : MovieStatus.SEARCHED;
    }
}

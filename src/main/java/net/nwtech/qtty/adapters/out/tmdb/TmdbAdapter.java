package net.nwtech.qtty.adapters.out.tmdb;

import info.movito.themoviedbapi.TmdbApi;
import info.movito.themoviedbapi.tools.TmdbException;
import net.nwtech.qtty.application.port.out.ImageStoragePort;
import net.nwtech.qtty.application.port.out.MovieRepositoryPort;
import lombok.RequiredArgsConstructor;
import net.nwtech.qtty.application.port.out.TmdbGateway;
import net.nwtech.qtty.domain.model.MovieModel;
import net.nwtech.qtty.domain.model.MovieStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class TmdbAdapter implements TmdbGateway {

    private final TmdbApi api;
    private final ImageStoragePort imageStoragePort;
    private final MovieRepositoryPort movieRepository;
    private final Logger logger = LoggerFactory.getLogger(TmdbAdapter.class);
    private final String API_PATH = "https://image.tmdb.org/t/p/original";

    @Override
    public List<MovieModel> searchMoviesFromTitle(String title) {
        List<MovieModel> movieModels = new ArrayList<>();
        try {
            var result = api.getSearch().searchMovie(title, true, null, null, null, null, null);
            logger.info("Found {} results for TMDB API call for title {}", result.getTotalResults(), title);
            int subListSize = Math.min(3, result.getResults().size()); // get top 3
            result.getResults().subList(0, subListSize).forEach(movie -> {
                long tmdbId = movie.getId();
                var existingMovie = movieRepository.findByTmdbId(tmdbId);
                movieModels.add(
                        MovieModel.builder()
                                .id(existingMovie.map(MovieModel::id).orElse(null))
                                .tmdbId(tmdbId)
                                .discordRatingMessageId(existingMovie.map(MovieModel::discordRatingMessageId).orElse(null))
                                .status(existingMovie.map(MovieModel::status).orElse(MovieStatus.SEARCHED))
                                .title(movie.getTitle())
                                .synopsis(movie.getOverview())
                                .imagePath(resolvePosterPath(existingMovie.orElse(null), movie.getPosterPath(), movie.getTitle()))
                                .releaseDate(movie.getReleaseDate())
                                .ratings(existingMovie.map(MovieModel::ratings).orElseGet(ArrayList::new))
                                .build()
                );
            });
        } catch (TmdbException e) {
            logger.error("Error trying to search movie by title - {}", e.getMessage());
        }
        return movieModels;
    }

    private String resolvePosterPath(MovieModel existingMovie, String posterPath, String movieTitle) {
        if (existingMovie != null && existingMovie.imagePath() != null && !existingMovie.imagePath().isBlank()) {
            return existingMovie.imagePath();
        }
        if (posterPath == null || posterPath.isBlank()) {
            return null;
        }

        String sourceUrl = API_PATH + posterPath;
        try {
            return imageStoragePort.storeFromUrl(sourceUrl, "movies", movieTitle);
        } catch (RuntimeException e) {
            logger.warn("Failed to mirror poster for movie {} into object storage. Continuing without stored image path.", movieTitle, e);
            return null;
        }
    }

}

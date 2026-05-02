package net.nwtech.qtty.application.port.out;

import net.nwtech.qtty.domain.model.MovieModel;
import net.nwtech.qtty.domain.model.RatingRecordModel;
import net.nwtech.qtty.entity.Movie;

import java.util.List;
import java.util.Optional;

public interface MovieRepositoryPort {

    Optional<MovieModel> findById(long id);

    Optional<MovieModel> findByTmdbId(long tmdbId);

    Optional<MovieModel> findByTitle(String title);

    MovieModel save(MovieModel movie);

    MovieModel updateRating(MovieModel movie, List<RatingRecordModel> ratingRecords);

    List<MovieModel> saveAll(List<MovieModel> movies);
}

package net.nwtech.qtty.application.port.out;

import net.nwtech.qtty.domain.model.MovieModel;
import net.nwtech.qtty.domain.model.RatingRecordModel;
import net.nwtech.qtty.entity.Movie;

import java.util.List;
import java.util.Optional;

public interface MovieRepositoryPort {

    Optional<Movie> findById(long id);

    Optional<Movie> findByTitle(String title);

    Movie save(MovieModel movie);

    Movie updateRating(MovieModel movie, List<RatingRecordModel> ratingRecords);
}

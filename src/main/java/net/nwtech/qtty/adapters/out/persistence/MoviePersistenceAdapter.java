package net.nwtech.qtty.adapters.out.persistence;

import lombok.RequiredArgsConstructor;
import net.nwtech.qtty.application.port.out.MovieRepositoryPort;
import net.nwtech.qtty.domain.model.MovieModel;
import net.nwtech.qtty.domain.model.RatingRecordModel;
import net.nwtech.qtty.entity.Movie;
import net.nwtech.qtty.repositories.MovieRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class MoviePersistenceAdapter implements MovieRepositoryPort {

    private final MovieRepository movieRepository;

    @Override
    public Optional<Movie> findById(long id) {
        return movieRepository.findById(id);
    }

    @Override
    public Optional<Movie> findByTitle(String title) {
        return movieRepository.findByTitle(title);
    }

    @Override
    public Movie save(MovieModel movie) {
        return null;
    }

    @Override
    public Movie updateRating(MovieModel movie, List<RatingRecordModel> ratingRecords) {
        return null;
    }


}

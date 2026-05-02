package net.nwtech.qtty.repositories;

import net.nwtech.qtty.entity.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

@Repository
public interface MovieRepository extends JpaRepository<Movie, Long> {

    @Query("""
        select distinct m
        from Movie m
        left join fetch m.ratings r
        left join fetch r.user
        where m.id = :id
    """)
    Optional<Movie> findByIdWithRatings(@Param("id") Long id);

    @Query("""
        select distinct m
        from Movie m
        left join fetch m.ratings r
        left join fetch r.user
        where m.title = :title
    """)
    Optional<Movie> findByTitleWithRatings(@Param("title") String title);

    @Query("""
        select distinct m
        from Movie m
        left join fetch m.ratings r
        left join fetch r.user
        where m.tmdbId = :tmdbId
    """)
    Optional<Movie> findByTmdbIdWithRatings(@Param("tmdbId") Long tmdbId);

    Optional<Movie> findByTmdbId(Long tmdbId);

    Optional<Movie> findByTitle(String title);

}

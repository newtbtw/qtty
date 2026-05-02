package net.nwtech.qtty.entity;

import jakarta.persistence.*;
import lombok.Data;
import net.nwtech.qtty.domain.model.MovieStatus;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table
@Data
public class Movie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private Long tmdbId;
    @Column(unique = true)
    private Long discordRatingMessageId;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MovieStatus status;
    private String title;
    @Column(columnDefinition = "TEXT")
    private String synopsis;
    private String imagePath;
    private String releaseDate;

    @OneToMany(mappedBy = "movie", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RatingRecord> ratings = new ArrayList<>();

    public void addRating(RatingRecord rating) {
        ratings.add(rating);
        rating.setMovie(this);
    }

    public void removeRating(RatingRecord rating) {
        ratings.remove(rating);
        rating.setMovie(null);
    }

}

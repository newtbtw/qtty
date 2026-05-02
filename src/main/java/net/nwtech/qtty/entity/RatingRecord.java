package net.nwtech.qtty.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"movie_id", "user_id"}))
@Data
public class RatingRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;
    private Double rating;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "movie_id")
    private Movie movie;

}

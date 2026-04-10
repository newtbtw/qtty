package net.nwtech.qtty.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Table
@Data
public class Movie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private Long discordRatingMessageId;
    private String name;
    @OneToMany(cascade = CascadeType.ALL)
    private List<RatingRecord> ratings;

}

package net.nwtech.qtty.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table
@Data
public class RatingRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;
    private Double rating;
    @ManyToOne(cascade = CascadeType.ALL)
    private Movie movie;

}

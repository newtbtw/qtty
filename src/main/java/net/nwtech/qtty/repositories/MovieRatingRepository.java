package net.nwtech.qtty.repositories;

import net.nwtech.qtty.entity.RatingRecord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MovieRatingRepository extends JpaRepository<RatingRecord, Long> {



}

package net.nwtech.qtty.domain.model;

import lombok.Builder;

@Builder
public record RatingRecordModel(
        Long id,
        UserModel user,
        Double rating,
        MovieModel movie
) {
}

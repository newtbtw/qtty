package net.nwtech.qtty.domain.model;

import lombok.Builder;

import java.util.List;

@Builder
public record MovieModel(
        Long id,
        Long discordRatingMessageId,
        String name,
        List<RatingRecordModel> ratings
) {
}

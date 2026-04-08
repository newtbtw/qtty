package net.nwtech.qtty.domain.model;

import lombok.Builder;

@Builder
public record GuildModel(
        Long id,
        long discordId,
        boolean allowed,
        String name,
        Long auditChannelId,
        Long moviesChannelId
) {
}

package net.nwtech.qtty.domain.model;

public record Role(
        Long id,
        long discordId,
        String roleName,
        Long guildId
) {
}

package net.nwtech.qtty.domain.model;

public record User(
        Long id,
        long discordId,
        String username
) {
}

package net.nwtech.qtty.domain.model;

public record Guild(
        Long id,
        long discordId,
        boolean allowed,
        String name
) {
}

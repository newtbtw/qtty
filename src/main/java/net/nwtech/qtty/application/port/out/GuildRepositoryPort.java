package net.nwtech.qtty.application.port.out;

import net.nwtech.qtty.domain.model.Guild;

import java.util.Optional;

public interface GuildRepositoryPort {

    Optional<Guild> findByDiscordId(long discordId);

    boolean existsByDiscordId(long discordId);

    Guild save(Guild guild);
}

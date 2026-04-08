package net.nwtech.qtty.application.port.out;

import net.nwtech.qtty.domain.model.GuildModel;

import java.util.Optional;

public interface GuildRepositoryPort {

    Optional<GuildModel> findByDiscordId(long discordId);

    boolean existsByDiscordId(long discordId);

    GuildModel save(GuildModel guildModel);
}

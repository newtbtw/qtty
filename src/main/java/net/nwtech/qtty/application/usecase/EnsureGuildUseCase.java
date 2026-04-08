package net.nwtech.qtty.application.usecase;

import lombok.RequiredArgsConstructor;
import net.nwtech.qtty.application.port.out.DiscordGateway;
import net.nwtech.qtty.application.port.out.GuildRepositoryPort;
import net.nwtech.qtty.domain.model.GuildModel;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EnsureGuildUseCase {

    private final GuildRepositoryPort guildRepository;
    private final DiscordGateway discordGateway;

    public GuildModel execute(long guildDiscordId) {
        return guildRepository.findByDiscordId(guildDiscordId)
                .orElseGet(() -> createGuild(guildDiscordId));
    }

    private GuildModel createGuild(long guildDiscordId) {
        var discordGuild = discordGateway.findGuildById(guildDiscordId)
                .orElseThrow(() -> new IllegalArgumentException("GuildModel not found on Discord: " + guildDiscordId));

        return guildRepository.save(new GuildModel(
                null,
                discordGuild.discordId(),
                false,
                discordGuild.name(),
                null,
                null
        ));
    }
}

package net.nwtech.qtty.application.usecase;

import lombok.RequiredArgsConstructor;
import net.nwtech.qtty.application.port.out.DiscordGateway;
import net.nwtech.qtty.application.port.out.GuildRepositoryPort;
import net.nwtech.qtty.domain.model.Guild;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EnsureGuildUseCase {

    private final GuildRepositoryPort guildRepository;
    private final DiscordGateway discordGateway;

    public Guild execute(long guildDiscordId) {
        return guildRepository.findByDiscordId(guildDiscordId)
                .orElseGet(() -> createGuild(guildDiscordId));
    }

    private Guild createGuild(long guildDiscordId) {
        var discordGuild = discordGateway.findGuildById(guildDiscordId)
                .orElseThrow(() -> new IllegalArgumentException("Guild not found on Discord: " + guildDiscordId));

        return guildRepository.save(new Guild(
                null,
                discordGuild.discordId(),
                false,
                discordGuild.name()
        ));
    }
}

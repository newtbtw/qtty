package net.nwtech.qtty.application.usecase;

import lombok.RequiredArgsConstructor;
import net.nwtech.qtty.application.port.out.GuildRepositoryPort;
import net.nwtech.qtty.domain.model.GuildModel;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UpdateGuildMoviesChannelCase {

    private final GuildRepositoryPort guildRepository;
    private final EnsureGuildUseCase ensureGuildCase;

    public void execute(long guildId, long channelId) {
        GuildModel guildModel = ensureGuildCase.execute(guildId);

        var updated = GuildModel.builder()
                .id(guildId)
                .discordId(guildModel.discordId())
                .allowed(guildModel.allowed())
                .name(guildModel.name())
                .auditChannelId(guildModel.auditChannelId())
                .moviesChannelId(channelId)
                .build();
        guildRepository.save(updated);
    }
}

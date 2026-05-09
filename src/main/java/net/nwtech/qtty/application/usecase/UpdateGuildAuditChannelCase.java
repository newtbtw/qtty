package net.nwtech.qtty.application.usecase;

import lombok.RequiredArgsConstructor;
import net.nwtech.qtty.application.port.out.GuildRepositoryPort;
import net.nwtech.qtty.domain.model.GuildModel;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UpdateGuildAuditChannelCase {

    private final GuildRepositoryPort guildRepository;
    private final EnsureGuildUseCase ensureGuildUseCase;

    public void execute(long guildId, long channelId) {
        GuildModel guildModel = ensureGuildUseCase.execute(guildId);

        var updated = GuildModel.builder()
                .id(guildId)
                .discordId(guildModel.discordId())
                .allowed(guildModel.allowed())
                .name(guildModel.name())
                .auditChannelId(channelId)
                .moviesChannelId(guildModel.moviesChannelId())
                .welcomeChannelId(guildModel.welcomeChannelId())
                .build();
        guildRepository.save(updated);
    }
}

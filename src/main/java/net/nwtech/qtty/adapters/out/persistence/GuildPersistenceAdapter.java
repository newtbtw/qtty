package net.nwtech.qtty.adapters.out.persistence;

import lombok.RequiredArgsConstructor;
import net.nwtech.qtty.application.port.out.GuildRepositoryPort;
import net.nwtech.qtty.domain.model.GuildModel;
import net.nwtech.qtty.repositories.GuildRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class GuildPersistenceAdapter implements GuildRepositoryPort {

    private final GuildRepository guildRepository;

    @Override
    public Optional<GuildModel> findByDiscordId(long discordId) {
        return guildRepository.findByDiscordId(discordId).map(this::toDomain);
    }

    @Override
    public boolean existsByDiscordId(long discordId) {
        return guildRepository.existsGuildByDiscordId(discordId);
    }

    @Override
    public GuildModel save(GuildModel guildModel) {
        net.nwtech.qtty.entity.Guild entity = guildModel.id() != null
                ? guildRepository.findById(guildModel.id()).orElse(new net.nwtech.qtty.entity.Guild())
                : new net.nwtech.qtty.entity.Guild();

        entity.setDiscordId(guildModel.discordId());
        entity.setAllowed(guildModel.allowed());
        entity.setName(guildModel.name());

        return toDomain(guildRepository.save(entity));
    }

    private GuildModel toDomain(net.nwtech.qtty.entity.Guild entity) {
        return new GuildModel(
                entity.getId(),
                entity.getDiscordId(),
                Boolean.TRUE.equals(entity.getAllowed()),
                entity.getName(),
                entity.getAuditChannelId(),
                entity.getMoviesChannelId()
        );
    }
}

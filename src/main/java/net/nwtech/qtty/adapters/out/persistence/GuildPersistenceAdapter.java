package net.nwtech.qtty.adapters.out.persistence;

import lombok.RequiredArgsConstructor;
import net.nwtech.qtty.application.port.out.GuildRepositoryPort;
import net.nwtech.qtty.domain.model.Guild;
import net.nwtech.qtty.repositories.GuildRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class GuildPersistenceAdapter implements GuildRepositoryPort {

    private final GuildRepository guildRepository;

    @Override
    public Optional<Guild> findByDiscordId(long discordId) {
        return guildRepository.findByDiscordId(discordId).map(this::toDomain);
    }

    @Override
    public boolean existsByDiscordId(long discordId) {
        return guildRepository.existsGuildByDiscordId(discordId);
    }

    @Override
    public Guild save(Guild guild) {
        net.nwtech.qtty.entity.Guild entity = guild.id() != null
                ? guildRepository.findById(guild.id()).orElse(new net.nwtech.qtty.entity.Guild())
                : new net.nwtech.qtty.entity.Guild();

        entity.setDiscordId(guild.discordId());
        entity.setAllowed(guild.allowed());
        entity.setName(guild.name());

        return toDomain(guildRepository.save(entity));
    }

    private Guild toDomain(net.nwtech.qtty.entity.Guild entity) {
        return new Guild(
                entity.getId(),
                entity.getDiscordId(),
                Boolean.TRUE.equals(entity.getAllowed()),
                entity.getName()
        );
    }
}

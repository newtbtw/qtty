package net.nwtech.qtty.adapters.out.persistence;

import lombok.RequiredArgsConstructor;
import net.nwtech.qtty.application.port.out.GuildRepositoryPort;
import net.nwtech.qtty.domain.model.GuildModel;
import net.nwtech.qtty.entity.Guild;
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
        Guild entity;

        if (existsByDiscordId(guildModel.discordId())) {
            // Guild already exists in database, fetch fresh and update
            Optional<Guild> existing = guildRepository.findByDiscordId(guildModel.discordId());
            if (existing.isPresent()) {
                Guild updated = existing.get();
                updated.setDiscordId(guildModel.discordId());
                updated.setAllowed(guildModel.allowed());
                updated.setName(guildModel.name());
                if (guildModel.auditChannelId() != null) {
                    updated.setAuditChannelId(guildModel.auditChannelId());
                }
                if (guildModel.moviesChannelId() != null) {
                    updated.setMoviesChannelId(guildModel.moviesChannelId());
                }
                entity = updated;
            } else {
                // Guild was marked as existing but not found - create new
                entity = new Guild(
                        guildModel.discordId(),
                        guildModel.allowed(),
                        guildModel.name()
                );
                if (guildModel.auditChannelId() != null) {
                    entity.setAuditChannelId(guildModel.auditChannelId());
                }
                if (guildModel.moviesChannelId() != null) {
                    entity.setMoviesChannelId(guildModel.moviesChannelId());
                }
            }
        } else {
            // New guild, create fresh instance
            entity = new Guild(
                    guildModel.discordId(),
                    guildModel.allowed(),
                    guildModel.name()
            );
            if (guildModel.auditChannelId() != null) {
                entity.setAuditChannelId(guildModel.auditChannelId());
            }
            if (guildModel.moviesChannelId() != null) {
                entity.setMoviesChannelId(guildModel.moviesChannelId());
            }
        }

        return toDomain(guildRepository.save(entity));
    }

    private GuildModel toDomain(Guild entity) {
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

package net.nwtech.qtty.repositories;

import net.nwtech.qtty.entity.Guild;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GuildRepository extends JpaRepository<Guild, Long> {

    Optional<Guild> findByDiscordId(Long discordId);

    boolean existsGuildByDiscordId(long guildId);
}

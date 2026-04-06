package net.nwtech.qtty.repositories;

import net.nwtech.qtty.entity.UserGuildProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserGuildProfileRepository extends JpaRepository<UserGuildProfile, Long> {

    Optional<UserGuildProfile> findByUser_IdAndGuild_Id(Long userId, Long guildId);
}

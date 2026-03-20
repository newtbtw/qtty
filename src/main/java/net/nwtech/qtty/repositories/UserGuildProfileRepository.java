package net.nwtech.qtty.repositories;

import net.nwtech.qtty.entity.UserGuildProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserGuildProfileRepository extends JpaRepository<UserGuildProfile, Long> {
}

package net.nwtech.qtty.repositories;

import net.nwtech.qtty.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByDiscordId(Long discordId);

    List<User> findByDiscordIdIn(Collection<Long> discordIds);

}

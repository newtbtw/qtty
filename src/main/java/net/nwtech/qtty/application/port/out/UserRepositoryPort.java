package net.nwtech.qtty.application.port.out;

import net.nwtech.qtty.domain.model.User;

import java.util.Optional;

public interface UserRepositoryPort {

    Optional<User> findByDiscordId(long discordId);

    User save(User user);
}

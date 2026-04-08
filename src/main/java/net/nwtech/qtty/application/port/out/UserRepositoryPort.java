package net.nwtech.qtty.application.port.out;

import net.nwtech.qtty.domain.model.UserModel;

import java.util.Optional;

public interface UserRepositoryPort {

    Optional<UserModel> findByDiscordId(long discordId);

    UserModel save(UserModel userModel);
}

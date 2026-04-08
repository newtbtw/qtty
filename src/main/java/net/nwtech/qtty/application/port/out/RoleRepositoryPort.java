package net.nwtech.qtty.application.port.out;

import net.nwtech.qtty.domain.model.RoleModel;

import java.util.Optional;

public interface RoleRepositoryPort {

    Optional<RoleModel> findByDiscordId(long discordId);

    RoleModel save(RoleModel roleModel);

    void deleteByDiscordId(long discordId);
}

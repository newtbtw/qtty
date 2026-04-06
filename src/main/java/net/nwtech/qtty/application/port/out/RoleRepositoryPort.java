package net.nwtech.qtty.application.port.out;

import net.nwtech.qtty.domain.model.Role;

import java.util.Optional;

public interface RoleRepositoryPort {

    Optional<Role> findByDiscordId(long discordId);

    Role save(Role role);

    void deleteByDiscordId(long discordId);
}

package net.nwtech.qtty.application.port.out;

import net.nwtech.qtty.domain.model.UserGuildProfile;

public interface UserGuildProfileRepositoryPort {

    UserGuildProfile save(UserGuildProfile profile);
}

package net.nwtech.qtty.application.port.out;

import net.nwtech.qtty.domain.model.UserGuildProfileModel;

public interface UserGuildProfileRepositoryPort {

    UserGuildProfileModel save(UserGuildProfileModel profile);
}

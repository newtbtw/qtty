package net.nwtech.qtty.domain.model;

import java.util.List;

public record UserGuildProfile(
        Long id,
        Long userId,
        Long guildId,
        String guildUserNickName,
        List<Role> roles
) {
}

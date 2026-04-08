package net.nwtech.qtty.domain.model;

import java.util.List;

public record UserGuildProfileModel(
        Long id,
        Long userId,
        Long guildId,
        String guildUserNickName,
        List<RoleModel> roleModels
) {
}

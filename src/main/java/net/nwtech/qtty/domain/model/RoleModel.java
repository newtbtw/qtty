package net.nwtech.qtty.domain.model;

public record RoleModel(
        Long id,
        long discordId,
        String roleName,
        Long guildId
) {
}

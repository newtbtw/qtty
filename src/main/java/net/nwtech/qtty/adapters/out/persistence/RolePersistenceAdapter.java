package net.nwtech.qtty.adapters.out.persistence;

import lombok.RequiredArgsConstructor;
import net.nwtech.qtty.application.port.out.RoleRepositoryPort;
import net.nwtech.qtty.domain.model.Role;
import net.nwtech.qtty.repositories.GuildRepository;
import net.nwtech.qtty.repositories.RoleRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class RolePersistenceAdapter implements RoleRepositoryPort {

    private final RoleRepository roleRepository;
    private final GuildRepository guildRepository;

    @Override
    public Optional<Role> findByDiscordId(long discordId) {
        return roleRepository.findByDiscordId(discordId).map(this::toDomain);
    }

    @Override
    public Role save(Role role) {
        net.nwtech.qtty.entity.Role entity = role.id() != null
                ? roleRepository.findById(role.id()).orElse(new net.nwtech.qtty.entity.Role())
                : new net.nwtech.qtty.entity.Role();

        var guild = guildRepository.findById(role.guildId())
                .orElseThrow(() -> new IllegalArgumentException("Guild not found for role: " + role.guildId()));

        entity.setDiscordId(role.discordId());
        entity.setRoleName(role.roleName());
        entity.setGuild(guild);

        return toDomain(roleRepository.save(entity));
    }

    @Override
    public void deleteByDiscordId(long discordId) {
        roleRepository.deleteByDiscordId(discordId);
    }

    private Role toDomain(net.nwtech.qtty.entity.Role entity) {
        return new Role(
                entity.getId(),
                entity.getDiscordId(),
                entity.getRoleName(),
                entity.getGuild().getId()
        );
    }
}

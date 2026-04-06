package net.nwtech.qtty.application.usecase;

import lombok.RequiredArgsConstructor;
import net.nwtech.qtty.application.port.out.RoleRepositoryPort;
import net.nwtech.qtty.domain.model.Role;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SyncRoleCreatedUseCase {

    private final EnsureGuildUseCase ensureGuildUseCase;
    private final RoleRepositoryPort roleRepository;

    @Transactional
    public Role execute(long guildDiscordId, long roleDiscordId, String roleName) {
        var guild = ensureGuildUseCase.execute(guildDiscordId);
        var existingRole = roleRepository.findByDiscordId(roleDiscordId);

        return roleRepository.save(new Role(
                existingRole.map(Role::id).orElse(null),
                roleDiscordId,
                roleName,
                guild.id()
        ));
    }
}

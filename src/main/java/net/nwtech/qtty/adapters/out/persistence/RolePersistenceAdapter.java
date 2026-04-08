package net.nwtech.qtty.adapters.out.persistence;

import lombok.RequiredArgsConstructor;
import net.nwtech.qtty.application.port.out.RoleRepositoryPort;
import net.nwtech.qtty.domain.model.RoleModel;
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
    public Optional<RoleModel> findByDiscordId(long discordId) {
        return roleRepository.findByDiscordId(discordId).map(this::toDomain);
    }

    @Override
    public RoleModel save(RoleModel roleModel) {
        net.nwtech.qtty.entity.Role entity = roleModel.id() != null
                ? roleRepository.findById(roleModel.id()).orElse(new net.nwtech.qtty.entity.Role())
                : new net.nwtech.qtty.entity.Role();

        var guild = guildRepository.findById(roleModel.guildId())
                .orElseThrow(() -> new IllegalArgumentException("GuildModel not found for roleModel: " + roleModel.guildId()));

        entity.setDiscordId(roleModel.discordId());
        entity.setRoleName(roleModel.roleName());
        entity.setGuild(guild);

        return toDomain(roleRepository.save(entity));
    }

    @Override
    public void deleteByDiscordId(long discordId) {
        roleRepository.deleteByDiscordId(discordId);
    }

    private RoleModel toDomain(net.nwtech.qtty.entity.Role entity) {
        return new RoleModel(
                entity.getId(),
                entity.getDiscordId(),
                entity.getRoleName(),
                entity.getGuild().getId()
        );
    }
}

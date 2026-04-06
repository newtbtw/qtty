package net.nwtech.qtty.adapters.out.persistence;

import lombok.RequiredArgsConstructor;
import net.nwtech.qtty.application.port.out.UserGuildProfileRepositoryPort;
import net.nwtech.qtty.domain.model.Role;
import net.nwtech.qtty.domain.model.UserGuildProfile;
import net.nwtech.qtty.repositories.GuildRepository;
import net.nwtech.qtty.repositories.RoleRepository;
import net.nwtech.qtty.repositories.UserGuildProfileRepository;
import net.nwtech.qtty.repositories.UserRepository;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserGuildProfilePersistenceAdapter implements UserGuildProfileRepositoryPort {

    private final UserGuildProfileRepository userGuildProfileRepository;
    private final UserRepository userRepository;
    private final GuildRepository guildRepository;
    private final RoleRepository roleRepository;

    @Override
    public UserGuildProfile save(UserGuildProfile profile) {
        net.nwtech.qtty.entity.UserGuildProfile entity = userGuildProfileRepository
                .findByUser_IdAndGuild_Id(profile.userId(), profile.guildId())
                .orElse(new net.nwtech.qtty.entity.UserGuildProfile());

        entity.setUser(userRepository.findById(profile.userId())
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + profile.userId())));
        entity.setGuild(guildRepository.findById(profile.guildId())
                .orElseThrow(() -> new IllegalArgumentException("Guild not found: " + profile.guildId())));
        entity.setGuildUserNickName(profile.guildUserNickName());
        entity.setRoles(roleRepository.findAllById(profile.roles().stream().map(Role::id).toList()));

        var saved = userGuildProfileRepository.save(entity);

        return new UserGuildProfile(
                saved.getId(),
                saved.getUser().getId(),
                saved.getGuild().getId(),
                saved.getGuildUserNickName(),
                saved.getRoles().stream()
                        .map(role -> new Role(role.getId(), role.getDiscordId(), role.getRoleName(), role.getGuild().getId()))
                        .toList()
        );
    }
}

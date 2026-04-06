package net.nwtech.qtty.application.usecase;

import lombok.RequiredArgsConstructor;
import net.nwtech.qtty.application.port.out.DiscordGateway;
import net.nwtech.qtty.application.port.out.RoleRepositoryPort;
import net.nwtech.qtty.application.port.out.UserGuildProfileRepositoryPort;
import net.nwtech.qtty.application.port.out.UserRepositoryPort;
import net.nwtech.qtty.domain.model.Guild;
import net.nwtech.qtty.domain.model.Role;
import net.nwtech.qtty.domain.model.User;
import net.nwtech.qtty.domain.model.UserGuildProfile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InitializeGuildUseCase {

    private final EnsureGuildUseCase ensureGuildUseCase;
    private final DiscordGateway discordGateway;
    private final UserRepositoryPort userRepository;
    private final RoleRepositoryPort roleRepository;
    private final UserGuildProfileRepositoryPort userGuildProfileRepository;

    @Transactional
    public InitializationResult execute(long guildDiscordId) {
        Guild guild = ensureGuildUseCase.execute(guildDiscordId);
        List<DiscordGateway.DiscordMember> members = discordGateway.getGuildMembers(guildDiscordId);

        int usersCreated = 0;
        int rolesCreated = 0;
        int profilesUpserted = 0;

        for (DiscordGateway.DiscordMember member : members) {
            var existingUser = userRepository.findByDiscordId(member.discordId());
            User user;
            if (existingUser.isPresent()) {
                user = existingUser.get();
            } else {
                user = userRepository.save(new User(null, member.discordId(), member.username()));
                usersCreated++;
            }

            List<Role> mappedRoles = new ArrayList<>();
            for (DiscordGateway.DiscordRole discordRole : member.roles()) {
                var existingRole = roleRepository.findByDiscordId(discordRole.discordId());
                Role role;
                if (existingRole.isPresent()) {
                    role = existingRole.get();
                } else {
                    role = roleRepository.save(new Role(
                            null,
                            discordRole.discordId(),
                            discordRole.name(),
                            guild.id()
                    ));
                    rolesCreated++;
                }
                mappedRoles.add(role);
            }

            userGuildProfileRepository.save(new UserGuildProfile(
                    null,
                    user.id(),
                    guild.id(),
                    member.nickname(),
                    mappedRoles
            ));
            profilesUpserted++;
        }

        return new InitializationResult(guild, members.size(), usersCreated, rolesCreated, profilesUpserted);
    }

    public record InitializationResult(
            Guild guild,
            int membersProcessed,
            int usersCreated,
            int rolesCreated,
            int profilesUpserted
    ) {
    }
}

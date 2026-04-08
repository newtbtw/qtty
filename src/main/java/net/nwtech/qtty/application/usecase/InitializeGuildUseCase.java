package net.nwtech.qtty.application.usecase;

import lombok.RequiredArgsConstructor;
import net.nwtech.qtty.application.port.out.DiscordGateway;
import net.nwtech.qtty.application.port.out.RoleRepositoryPort;
import net.nwtech.qtty.application.port.out.UserGuildProfileRepositoryPort;
import net.nwtech.qtty.application.port.out.UserRepositoryPort;
import net.nwtech.qtty.domain.model.*;
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
        GuildModel guildModel = ensureGuildUseCase.execute(guildDiscordId);
        List<DiscordGateway.DiscordMember> members = discordGateway.getGuildMembers(guildDiscordId);

        int usersCreated = 0;
        int rolesCreated = 0;
        int profilesUpserted = 0;

        for (DiscordGateway.DiscordMember member : members) {
            var existingUser = userRepository.findByDiscordId(member.discordId());
            UserModel userModel;
            if (existingUser.isPresent()) {
                userModel = existingUser.get();
            } else {
                userModel = userRepository.save(new UserModel(null, member.discordId(), member.username()));
                usersCreated++;
            }

            List<RoleModel> mappedRoleModels = new ArrayList<>();
            for (DiscordGateway.DiscordRole discordRole : member.roles()) {
                var existingRole = roleRepository.findByDiscordId(discordRole.discordId());
                RoleModel roleModel;
                if (existingRole.isPresent()) {
                    roleModel = existingRole.get();
                } else {
                    roleModel = roleRepository.save(new RoleModel(
                            null,
                            discordRole.discordId(),
                            discordRole.name(),
                            guildModel.id()
                    ));
                    rolesCreated++;
                }
                mappedRoleModels.add(roleModel);
            }

            userGuildProfileRepository.save(new UserGuildProfileModel(
                    null,
                    userModel.id(),
                    guildModel.id(),
                    member.nickname(),
                    mappedRoleModels
            ));
            profilesUpserted++;
        }

        return new InitializationResult(guildModel, members.size(), usersCreated, rolesCreated, profilesUpserted);
    }

    public record InitializationResult(
            GuildModel guildModel,
            int membersProcessed,
            int usersCreated,
            int rolesCreated,
            int profilesUpserted
    ) {
    }
}

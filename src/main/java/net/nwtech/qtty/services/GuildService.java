package net.nwtech.qtty.services;

import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.Member;
import net.nwtech.qtty.discord.services.DiscordService;
import net.nwtech.qtty.entity.Guild;
import net.nwtech.qtty.entity.Role;
import net.nwtech.qtty.entity.User;
import net.nwtech.qtty.entity.UserGuildProfile;
import net.nwtech.qtty.repositories.GuildRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@EnableAsync
public class GuildService {

    private final GuildRepository guildRepository;
    private final RoleService roleService;
    private final UserService userService;
    private final UserGuildProfileService userGuildProfileService;
    private final DiscordService discordService;
    private final Logger logger =  LoggerFactory.getLogger(this.getClass());

    public Guild getOrCreate(@NotNull Long guildDiscordId) {
        var opt = guildRepository.findById(guildDiscordId);
        return opt.orElseGet(() -> createGuild(guildDiscordId));
    }

    private Guild createGuild(@NotNull long guildDiscordId) {
        var discord_guild = discordService.getGuild(guildDiscordId);
        if (discord_guild.isEmpty()) {
            throw new IllegalArgumentException("Guild not found");
        }
        var optionalRoles = discordService.getAllGuildRoles(guildDiscordId);
        var roles = optionalRoles.orElseGet(ArrayList::new);
        List<Role> rolesList = new ArrayList<>(roles.size());
        for (var role : roles) {
            rolesList.add(new Role(role.getIdLong(),  role.getName()));
        }
        List<Member> members = discordService.getAllGuildMembers(guildDiscordId).orElseGet(ArrayList::new);
        List<User> users = new ArrayList<>(members.size());
        List<UserGuildProfile> userGuildProfiles = new ArrayList<>(users.size());
        var guild = new Guild(guildDiscordId, rolesList, false);

        for (var member : members) {
            User user = new User(member.getIdLong());
            users.add(user);
            List<Role> memberRoles = rolesList.stream()
                    .filter(r -> r.getDiscordId().equals(role.getIdLong()))
                    .collect(Collectors.toList());

            userGuildProfiles.add(UserGuildProfile.builder()
                    .guildNickName(member.getEffectiveName())
                    .user(user)
                    .roles(memberRoles)
                    .guild(guild)
                    .build());
        }

        // Save all users first
        userService.saveAll(users);
        var dbGuild = guildRepository.save(guild);

        // Then save all user guild profiles
        userGuildProfileService.saveAll(userGuildProfiles);

        return dbGuild;
    }

}

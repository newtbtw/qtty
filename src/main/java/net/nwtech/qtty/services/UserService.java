package net.nwtech.qtty.services;

import lombok.RequiredArgsConstructor;
import net.nwtech.qtty.discord.services.DiscordService;
import net.nwtech.qtty.entity.Guild;
import net.nwtech.qtty.entity.User;
import net.nwtech.qtty.entity.UserGuildProfile;
import net.nwtech.qtty.repositories.UserGuildProfileRepository;
import net.nwtech.qtty.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserGuildProfileRepository profileRepository;
    private final DiscordService discordService;
    private final RoleService roleService;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public void saveAll(List<User> users) {
        userRepository.saveAll(users);
    }

    public List<User> findByDiscordIds(Collection<Long> discordIds) {
        return userRepository.findByDiscordIdIn(discordIds);
    }

    public User save(User user) {
        return userRepository.save(user);
    }

    public void addToGuild(List<Long> usersId, Guild guild) {
        logger.info("Adding {} users to guildModel: {} [{}]", usersId.size(), guild.getName(), guild.getDiscordId());
        int created = 0;
        int addedToGuild = 0;
        int notFound = 0;
        int profilesCreated = 0;
        for (Long userId : usersId) {
            User user = getOrCreate(guild.getDiscordId(), userId);
            if (user == null){
                notFound++;
                continue;
            }
            else
                created++;
            UserGuildProfile profile = createProfileFromUser(guild, user);
            if (profile != null){
                profilesCreated++;
                profileRepository.save(profile);
            }
        }
        logger.info("{} users created in db. {} users not found. {} users added to guildModel. {} profiles created.", created, notFound, addedToGuild, profilesCreated);
    }

    private UserGuildProfile createProfileFromUser(Guild guild, User user) {
        var member = discordService.getGuildMember(guild.getDiscordId(), user.getDiscordId());
        if (member.isEmpty())
            return null;
        var m = member.get();
        var memberRoles = m.getRoles();
        var nick = m.getNickname();
        var roles = roleService.getOrCreateRoles(memberRoles);
        return UserGuildProfile
                .builder()
                .roles(roles)
                .user(user)
                .guild(guild)
                .guildUserNickName(nick)
                .build();
    }

    public User getOrCreate(long guildId,  long userId) {
        var discordUser = discordService.getGuildMember(guildId, userId);
        return discordUser.map(member -> save(new User(member.getIdLong(), member.getUser().getGlobalName()))).orElse(null);
    }
}

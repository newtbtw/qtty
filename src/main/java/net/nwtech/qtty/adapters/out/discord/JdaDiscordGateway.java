package net.nwtech.qtty.adapters.out.discord;

import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.JDA;
import net.nwtech.qtty.application.port.out.DiscordGateway;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class JdaDiscordGateway implements DiscordGateway {

    private final JDA jda;

    @Override
    public Optional<DiscordGuild> findGuildById(long guildId) {
        return Optional.ofNullable(jda.getGuildById(guildId))
                .map(guild -> new DiscordGuild(guild.getIdLong(), guild.getName()));
    }

    @Override
    public List<DiscordMember> getGuildMembers(long guildId) {
        var guild = jda.getGuildById(guildId);
        if (guild == null) {
            return List.of();
        }
        return guild.getMembers().stream()
                .map(member -> new DiscordMember(
                        member.getIdLong(),
                        member.getUser().getGlobalName() != null ? member.getUser().getGlobalName() : member.getUser().getName(),
                        member.getNickname(),
                        member.getRoles().stream()
                                .map(role -> new DiscordRole(role.getIdLong(), role.getName(), guildId))
                                .toList()
                ))
                .toList();
    }

    @Override
    public Optional<DiscordMember> findGuildMember(long guildId, long userId) {
        var guild = jda.getGuildById(guildId);
        if (guild == null) {
            return Optional.empty();
        }

        return Optional.ofNullable(guild.getMemberById(userId))
                .map(member -> new DiscordMember(
                        member.getIdLong(),
                        member.getUser().getName(),
                        member.getNickname(),
                        member.getRoles().stream()
                                .map(role -> new DiscordRole(role.getIdLong(), role.getName(), guildId))
                                .toList()
                ));
    }
}

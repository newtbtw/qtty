package net.nwtech.qtty.discord.services;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.nwtech.qtty.discord.config.Discord;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DiscordService {

    private final JDA jda = Discord.JDA_CLIENT;

    public Optional<Guild> getGuild(long guildId) {
        return Optional.ofNullable(jda.getGuildById(guildId));
    }

    public Optional<List<Member>> getAllGuildMembers(long guildId) {
        var g =  jda.getGuildById(guildId);
        if (g == null) return Optional.empty();
        return Optional.of(g.getMembers());
    }

    public Optional<List<Role>> getAllGuildRoles(long guildId) {
        var g =  jda.getGuildById(guildId);
        if (g == null) return Optional.empty();
        return Optional.of(g.getRoles());
    }

}

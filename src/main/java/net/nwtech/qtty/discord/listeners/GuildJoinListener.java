package net.nwtech.qtty.discord.listeners;

import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.nwtech.qtty.discord.services.DiscordService;
import net.nwtech.qtty.services.GuildService;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GuildJoinListener extends ListenerAdapter implements IListener {

    private final GuildService guildService;
    private final Logger logger =  LoggerFactory.getLogger(this.getClass());

    @Override
    public void onGuildJoin(@NotNull GuildJoinEvent event) {
       var g = guildService.getOrCreate(event.getGuild().getIdLong());
       logger.info("Joined on guild {} ({})", event.getGuild().getName(), event.getGuild().getIdLong());
    }
}

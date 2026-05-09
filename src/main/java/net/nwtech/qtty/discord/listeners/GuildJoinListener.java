package net.nwtech.qtty.discord.listeners;

import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.nwtech.qtty.adapters.out.persistence.GuildPersistenceAdapter;
import net.nwtech.qtty.application.usecase.AskSetUpUseCase;
import net.nwtech.qtty.application.usecase.EnsureGuildUseCase;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GuildJoinListener extends ListenerAdapter implements IListener {

    private final EnsureGuildUseCase ensureGuildUseCase;
    private final AskSetUpUseCase askSetupCase;
    private final Logger LOGGER =  LoggerFactory.getLogger(this.getClass());

    @Override
    public void onGuildJoin(@NotNull GuildJoinEvent event) {
        ensureGuildUseCase.execute(event.getGuild().getIdLong());
        askSetupCase.execute(event);
        LOGGER.info("Joined on guildModel {} ({})", event.getGuild().getName(), event.getGuild().getIdLong());
    }
}

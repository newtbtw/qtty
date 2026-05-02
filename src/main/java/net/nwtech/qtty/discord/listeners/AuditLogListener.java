package net.nwtech.qtty.discord.listeners;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.audit.ActionType;
import net.dv8tion.jda.api.audit.AuditLogEntry;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.guild.GuildAuditLogEntryCreateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.nwtech.qtty.adapters.out.discord.JdaDiscordGateway;
import net.nwtech.qtty.adapters.out.persistence.GuildPersistenceAdapter;
import net.nwtech.qtty.discord.audit.IMessageFormatter;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class AuditLogListener extends ListenerAdapter implements IListener {

    private final GuildPersistenceAdapter guildPersistenceAdapter;
    private final JdaDiscordGateway jda;
    private final Logger LOGGER =  LoggerFactory.getLogger(AuditLogListener.class);
    private final List<IMessageFormatter>  messageFormatters;

    @Override
    public void onGuildAuditLogEntryCreate(@NotNull GuildAuditLogEntryCreateEvent event) {
        long guildId = event.getGuild().getIdLong();
        var guild = guildPersistenceAdapter.findByDiscordId(guildId);
        ActionType action = event.getEntry().getType();
        if (guild.isEmpty()) return;
        long auditChannelId = guild.get().auditChannelId();
        if (auditChannelId == 0) return;
        var auditChannel = event.getGuild().getChannelById(TextChannel.class, auditChannelId);
        if (auditChannel == null) {
            LOGGER.warn("Audit channel with id {} not found for guild id {}", auditChannelId, guildId);
            return;
        }
        messageFormatters.stream().filter(mf -> mf.handles(action)).findFirst().ifPresentOrElse( mf -> {
            auditChannel.sendMessageComponents(mf.buildLogMessage(event)).useComponentsV2().queue();
        }, () -> {
            LOGGER.warn("No handler for {}", action);
        });

    }

}

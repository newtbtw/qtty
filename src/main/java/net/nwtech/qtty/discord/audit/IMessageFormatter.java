package net.nwtech.qtty.discord.audit;

import net.dv8tion.jda.api.audit.ActionType;
import net.dv8tion.jda.api.components.MessageTopLevelComponent;
import net.dv8tion.jda.api.events.guild.GuildAuditLogEntryCreateEvent;

public interface IMessageFormatter {

    MessageTopLevelComponent buildLogMessage(GuildAuditLogEntryCreateEvent event);

    boolean handles(ActionType type);

}

package net.nwtech.qtty.discord.audit;

import net.dv8tion.jda.api.audit.ActionType;
import net.dv8tion.jda.api.audit.AuditLogChange;
import net.dv8tion.jda.api.audit.AuditLogKey;
import net.dv8tion.jda.api.components.MessageTopLevelComponent;
import net.dv8tion.jda.api.components.container.Container;
import net.dv8tion.jda.api.components.separator.Separator;
import net.dv8tion.jda.api.components.textdisplay.TextDisplay;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.guild.GuildAuditLogEntryCreateEvent;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.util.List;
import java.util.Map;

@Component
public class ChannelUpdatedFormatter implements IMessageFormatter {

    private static final Map<AuditLogKey, String> TITLES = Map.of(
            AuditLogKey.CHANNEL_NAME, "Channel name changed!",
            AuditLogKey.CHANNEL_USER_LIMIT, "Channel user limit changed!",
            AuditLogKey.CHANNEL_TOPIC, "Channel topic changed!"
    );

    private static final List<AuditLogKey> SUPPORTED_KEYS = List.of(
            AuditLogKey.CHANNEL_NAME,
            AuditLogKey.CHANNEL_USER_LIMIT,
            AuditLogKey.CHANNEL_TOPIC
    );

    @Override
    public MessageTopLevelComponent buildLogMessage(GuildAuditLogEntryCreateEvent event) {
        var entry = event.getEntry();
        var changes = entry.getChangesForKeys(SUPPORTED_KEYS.toArray(AuditLogKey[]::new));
        var actor = event.getJDA().retrieveUserById(entry.getUserIdLong()).complete();

        String title = changes.stream()
                .findFirst()
                .map(change -> TITLES.getOrDefault(resolveKey(change), "Channel updated!"))
                .orElse("Channel updated!");

        String channelName = event.getGuild()
                .getGuildChannelById(entry.getTargetIdLong()) != null
                ? event.getGuild().getGuildChannelById(entry.getTargetIdLong()).getName()
                : "Unknown channel";

        StringBuilder details = new StringBuilder()
                .append("## #")
                .append(channelName)
                .append("\n### Updated by: ")
                .append(resolveUserName(actor))
                .append("\n");

        if (entry.getReason() != null && !entry.getReason().isBlank()) {
            details.append("### Reason: ").append(entry.getReason()).append("\n");
        }

        if (changes.isEmpty()) {
            details.append("### No supported field changes were found.");
        } else {
            for (AuditLogChange change : changes) {
                details.append("### ")
                        .append(formatFieldName(change))
                        .append(": ")
                        .append(formatValue(change.getOldValue()))
                        .append(" -> ")
                        .append(formatValue(change.getNewValue()))
                        .append("\n");
            }
        }

        return Container.of(
                TextDisplay.of("# " + title + "\n"),
                Separator.create(true, Separator.Spacing.SMALL),
                TextDisplay.of(details.toString().trim())
        ).withAccentColor(Color.ORANGE);
    }

    @Override
    public boolean handles(ActionType type) {
        return ActionType.CHANNEL_UPDATE.equals(type);
    }

    private AuditLogKey resolveKey(AuditLogChange change) {
        return SUPPORTED_KEYS.stream()
                .filter(key -> key.getKey().equalsIgnoreCase(change.getKey()))
                .findFirst()
                .orElse(null);
    }

    private String formatFieldName(AuditLogChange change) {
        AuditLogKey key = resolveKey(change);
        if (AuditLogKey.CHANNEL_NAME.equals(key)) return "Name";
        if (AuditLogKey.CHANNEL_TOPIC.equals(key)) return "Topic";
        if (AuditLogKey.CHANNEL_USER_LIMIT.equals(key)) return "User limit";
        return change.getKey();
    }

    private String formatValue(Object value) {
        return value == null ? "not set" : String.valueOf(value);
    }

    private String resolveUserName(User user) {
        if (user.getGlobalName() != null && !user.getGlobalName().isBlank()) {
            return user.getEffectiveName();
        }
        return user.getName();
    }
}

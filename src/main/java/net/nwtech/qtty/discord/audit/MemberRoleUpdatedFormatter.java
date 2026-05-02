package net.nwtech.qtty.discord.audit;

import net.dv8tion.jda.api.audit.ActionType;
import net.dv8tion.jda.api.audit.AuditLogKey;
import net.dv8tion.jda.api.components.MessageTopLevelComponent;
import net.dv8tion.jda.api.components.container.Container;
import net.dv8tion.jda.api.components.separator.Separator;
import net.dv8tion.jda.api.components.textdisplay.TextDisplay;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.guild.GuildAuditLogEntryCreateEvent;
import net.dv8tion.jda.api.utils.data.DataArray;
import net.dv8tion.jda.api.utils.data.DataObject;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class MemberRoleUpdatedFormatter implements IMessageFormatter {

    @Override
    public MessageTopLevelComponent buildLogMessage(GuildAuditLogEntryCreateEvent event) {
        var entry = event.getEntry();
        var actor = event.getJDA().retrieveUserById(entry.getUserIdLong()).complete();
        var target = event.getJDA().retrieveUserById(entry.getTargetIdLong()).complete();
        var addedChange = entry.getChangeByKey(AuditLogKey.MEMBER_ROLES_ADD);
        var removedChange = entry.getChangeByKey(AuditLogKey.MEMBER_ROLES_REMOVE);

        List<String> addedRoles = extractRoleNames(addedChange == null ? null : firstNonNull(addedChange.getNewValue(), addedChange.getOldValue()));
        List<String> removedRoles = extractRoleNames(removedChange == null ? null : firstNonNull(removedChange.getNewValue(), removedChange.getOldValue()));

        StringBuilder body = new StringBuilder()
                .append("## Member: ")
                .append(resolveUserName(target))
                .append("\n### Updated by: ")
                .append(resolveUserName(actor))
                .append("\n");

        if (entry.getReason() != null && !entry.getReason().isBlank()) {
            body.append("### Reason: ").append(entry.getReason()).append("\n");
        }

        if (!addedRoles.isEmpty()) {
            body.append("### Roles added: ").append(String.join(", ", addedRoles)).append("\n");
        }

        if (!removedRoles.isEmpty()) {
            body.append("### Roles removed: ").append(String.join(", ", removedRoles)).append("\n");
        }

        if (addedRoles.isEmpty() && removedRoles.isEmpty()) {
            body.append("### No role changes were found.");
        }

        return Container.of(
                TextDisplay.of("# Member roles updated!\n"),
                Separator.create(true, Separator.Spacing.SMALL),
                TextDisplay.of(body.toString().trim())
        ).withAccentColor(Color.YELLOW);
    }

    @Override
    public boolean handles(ActionType type) {
        return ActionType.MEMBER_ROLE_UPDATE.equals(type);
    }

    private Object firstNonNull(Object preferred, Object fallback) {
        return preferred != null ? preferred : fallback;
    }

    private List<String> extractRoleNames(Object value) {
        if (value == null) {
            return List.of();
        }

        if (value instanceof DataArray dataArray) {
            List<String> roles = new ArrayList<>();
            for (int i = 0; i < dataArray.length(); i++) {
                if (!dataArray.isNull(i)) {
                    roles.add(extractRoleName(dataArray.getObject(i)));
                }
            }
            return roles.stream().filter(name -> !name.isBlank()).toList();
        }

        if (value instanceof Collection<?> collection) {
            return collection.stream()
                    .map(this::extractRoleName)
                    .filter(name -> !name.isBlank())
                    .toList();
        }

        return List.of(String.valueOf(value));
    }

    private String extractRoleName(Object value) {
        if (value instanceof DataObject dataObject) {
            return formatRoleName(dataObject.getString("name", dataObject.getString("id", "Unknown role")));
        }

        if (value instanceof Map<?, ?> map) {
            Object roleName = map.get("name");
            Object roleId = map.get("id");
            return formatRoleName(roleName != null ? String.valueOf(roleName) : String.valueOf(roleId));
        }

        return formatRoleName(String.valueOf(value));
    }

    private String formatRoleName(String roleName) {
        if (roleName == null || roleName.isBlank()) {
            return "";
        }
        return "@" + roleName;
    }

    private String resolveUserName(User user) {
        if (user.getGlobalName() != null && !user.getGlobalName().isBlank()) {
            return user.getGlobalName();
        }
        return user.getName();
    }
}

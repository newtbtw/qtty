package net.nwtech.qtty.discord.audit;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.audit.ActionType;
import net.dv8tion.jda.api.audit.AuditLogKey;
import net.dv8tion.jda.api.audit.AuditLogOption;
import net.dv8tion.jda.api.components.MessageTopLevelComponent;
import net.dv8tion.jda.api.components.container.Container;
import net.dv8tion.jda.api.components.separator.Separator;
import net.dv8tion.jda.api.components.textdisplay.TextDisplay;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.PermissionOverride;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.entities.channel.attribute.IPermissionContainer;
import net.dv8tion.jda.api.events.guild.GuildAuditLogEntryCreateEvent;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class ChannelOverrideFormatter implements IMessageFormatter {

    @Override
    public MessageTopLevelComponent buildLogMessage(GuildAuditLogEntryCreateEvent event) {
        var entry = event.getEntry();
        var actionType = entry.getType();
        var actor = event.getJDA().retrieveUserById(entry.getUserIdLong()).complete();
        var channel = event.getGuild().getGuildChannelById(entry.getTargetIdLong());
        var allowChange = entry.getChangeByKey(AuditLogKey.OVERRIDE_ALLOW);
        var denyChange = entry.getChangeByKey(AuditLogKey.OVERRIDE_DENY);

        StringBuilder body = new StringBuilder()
                .append("## Channel: ")
                .append(resolveChannelName(channel))
                .append("\n### Target: ")
                .append(resolveOverrideTarget(event, channel, allowChange, denyChange))
                .append("\n### Updated by: ")
                .append(resolveUserName(actor))
                .append("\n");

        if (entry.getReason() != null && !entry.getReason().isBlank()) {
            body.append("### Reason: ").append(entry.getReason()).append("\n");
        }

        appendPermissionSection(body, "Allowed", allowChange == null ? null : allowChange.getOldValue(), allowChange == null ? null : allowChange.getNewValue(), actionType);
        appendPermissionSection(body, "Denied", denyChange == null ? null : denyChange.getOldValue(), denyChange == null ? null : denyChange.getNewValue(), actionType);

        return Container.of(
                TextDisplay.of("# " + resolveTitle(actionType) + "\n"),
                Separator.create(true, Separator.Spacing.SMALL),
                TextDisplay.of(body.toString().trim())
        ).withAccentColor(Color.ORANGE);
    }

    @Override
    public boolean handles(ActionType type) {
        return ActionType.CHANNEL_OVERRIDE_CREATE.equals(type)
                || ActionType.CHANNEL_OVERRIDE_UPDATE.equals(type)
                || ActionType.CHANNEL_OVERRIDE_DELETE.equals(type);
    }

    private void appendPermissionSection(StringBuilder body, String label, Object oldValue, Object newValue, ActionType actionType) {
        long oldBits = toLong(oldValue);
        long newBits = toLong(newValue);

        if (ActionType.CHANNEL_OVERRIDE_CREATE.equals(actionType)) {
            body.append("### ").append(label).append(": ").append(formatPermissions(newBits)).append("\n");
            return;
        }

        if (ActionType.CHANNEL_OVERRIDE_DELETE.equals(actionType)) {
            body.append("### ").append(label).append(": ").append(formatPermissions(oldBits)).append("\n");
            return;
        }

        Set<Permission> added = Permission.getPermissions(newBits).stream()
                .filter(permission -> !Permission.getPermissions(oldBits).contains(permission))
                .collect(Collectors.toSet());

        Set<Permission> removed = Permission.getPermissions(oldBits).stream()
                .filter(permission -> !Permission.getPermissions(newBits).contains(permission))
                .collect(Collectors.toSet());

        if (!added.isEmpty()) {
            body.append("### ").append(label).append(" added: ").append(formatPermissions(added)).append("\n");
        }
        if (!removed.isEmpty()) {
            body.append("### ").append(label).append(" removed: ").append(formatPermissions(removed)).append("\n");
        }
        if (added.isEmpty() && removed.isEmpty()) {
            body.append("### ").append(label).append(": no changes detected\n");
        }
    }

    private String resolveTitle(ActionType type) {
        if (ActionType.CHANNEL_OVERRIDE_CREATE.equals(type)) return "Channel permission override created!";
        if (ActionType.CHANNEL_OVERRIDE_DELETE.equals(type)) return "Channel permission override deleted!";
        return "Channel permission override updated!";
    }

    private String resolveOverrideTarget(GuildAuditLogEntryCreateEvent event, GuildChannel channel, Object allowChange, Object denyChange) {
        var entry = event.getEntry();
        String roleId = entry.getOption(AuditLogOption.ROLE);
        if (roleId != null) {
            Role role = event.getGuild().getRoleById(roleId);
            return role != null ? "@"+ role.getName() : "Role " + roleId;
        }

        String userId = entry.getOption(AuditLogOption.USER);
        if (userId != null) {
            User user = event.getJDA().retrieveUserById(userId).complete();
            return resolveUserName(user);
        }

        String memberId = entry.getOptionByName("member");
        if (memberId != null) {
            Member member = event.getGuild().getMemberById(memberId);
            if (member != null) {
                return resolveUserName(member.getUser());
            }
            User user = event.getJDA().retrieveUserById(memberId).complete();
            return resolveUserName(user);
        }

        PermissionOverride override = findMatchingOverride(channel, entry.getType(),
                allowChange == null ? null : ((net.dv8tion.jda.api.audit.AuditLogChange) allowChange).getOldValue(),
                allowChange == null ? null : ((net.dv8tion.jda.api.audit.AuditLogChange) allowChange).getNewValue(),
                denyChange == null ? null : ((net.dv8tion.jda.api.audit.AuditLogChange) denyChange).getOldValue(),
                denyChange == null ? null : ((net.dv8tion.jda.api.audit.AuditLogChange) denyChange).getNewValue());
        if (override != null) {
            if (override.isRoleOverride() && override.getRole() != null) {
                return "@" + override.getRole().getName();
            }
            if (override.isMemberOverride() && override.getMember() != null) {
                return resolveUserName(override.getMember().getUser());
            }
        }

        return "Unknown target";
    }

    private PermissionOverride findMatchingOverride(GuildChannel channel, ActionType actionType,
                                                    Object oldAllowValue, Object newAllowValue,
                                                    Object oldDenyValue, Object newDenyValue) {
        if (!(channel instanceof IPermissionContainer permissionContainer)) {
            return null;
        }

        long oldAllow = toLong(oldAllowValue);
        long newAllow = toLong(newAllowValue);
        long oldDeny = toLong(oldDenyValue);
        long newDeny = toLong(newDenyValue);

        return permissionContainer.getPermissionOverrides().stream()
                .filter(override -> matchesOverride(override, actionType, oldAllow, newAllow, oldDeny, newDeny))
                .findFirst()
                .orElse(null);
    }

    private boolean matchesOverride(PermissionOverride override, ActionType actionType,
                                    long oldAllow, long newAllow, long oldDeny, long newDeny) {
        long overrideAllow = override.getAllowedRaw();
        long overrideDeny = override.getDeniedRaw();

        if (ActionType.CHANNEL_OVERRIDE_CREATE.equals(actionType)) {
            return overrideAllow == newAllow && overrideDeny == newDeny;
        }

        if (ActionType.CHANNEL_OVERRIDE_DELETE.equals(actionType)) {
            return overrideAllow == oldAllow && overrideDeny == oldDeny;
        }

        boolean matchesNew = overrideAllow == newAllow && overrideDeny == newDeny;
        boolean changedAllow = oldAllow != newAllow;
        boolean changedDeny = oldDeny != newDeny;

        if (!matchesNew) {
            return false;
        }

        if (changedAllow && changedDeny) {
            return true;
        }

        if (changedAllow) {
            return overrideDeny == (newDeny != 0L ? newDeny : oldDeny);
        }

        if (changedDeny) {
            return overrideAllow == (newAllow != 0L ? newAllow : oldAllow);
        }

        return true;
    }

    private String resolveChannelName(GuildChannel channel) {
        return channel != null ? "#" + channel.getName() : "Unknown channel";
    }

    private String resolveUserName(User user) {
        if (user.getGlobalName() != null && !user.getGlobalName().isBlank()) {
            return user.getGlobalName();
        }
        return user.getName();
    }

    private long toLong(Object value) {
        if (value instanceof Number number) {
            return number.longValue();
        }
        if (value instanceof String stringValue && !stringValue.isBlank()) {
            return Long.parseLong(stringValue);
        }
        return 0L;
    }

    private String formatPermissions(Object bits) {
        return formatPermissions(Permission.getPermissions(toLong(bits)));
    }

    private String formatPermissions(Set<Permission> permissions) {
        if (permissions == null || permissions.isEmpty()) {
            return "none";
        }

        return permissions.stream()
                .filter(permission -> !Permission.UNKNOWN.equals(permission))
                .map(Permission::getName)
                .sorted()
                .collect(Collectors.joining(", "));
    }
}

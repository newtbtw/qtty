package net.nwtech.qtty.discord.audit;

import net.dv8tion.jda.api.audit.ActionType;
import net.dv8tion.jda.api.components.MessageTopLevelComponent;
import net.dv8tion.jda.api.components.container.Container;
import net.dv8tion.jda.api.components.mediagallery.MediaGallery;
import net.dv8tion.jda.api.components.mediagallery.MediaGalleryItem;
import net.dv8tion.jda.api.components.separator.Separator;
import net.dv8tion.jda.api.components.textdisplay.TextDisplay;
import net.dv8tion.jda.api.events.guild.GuildAuditLogEntryCreateEvent;
import org.springframework.stereotype.Component;

import java.awt.*;

@Component
public class KickMessageFormatter implements IMessageFormatter {

    @Override
    public MessageTopLevelComponent buildLogMessage(GuildAuditLogEntryCreateEvent event) {

        var discordUserId = event.getEntry().getTargetIdLong();
        var user = event.getJDA().retrieveUserById(discordUserId).complete();

       return Container.of(
               TextDisplay.of("# User kicked!"),
               Separator.create(true, Separator.Spacing.SMALL),
               TextDisplay.of("## " + user.getGlobalName() + "\n### Reason: " + event.getEntry().getReason()),
               MediaGallery.of(MediaGalleryItem.fromUrl(user.getEffectiveAvatarUrl()))
       ).withAccentColor(Color.RED);
    }

    @Override
    public boolean handles(ActionType type) {
        return ActionType.KICK.equals(type);
    }
}

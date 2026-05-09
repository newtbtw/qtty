package net.nwtech.qtty.application.usecase;

import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.components.MessageTopLevelComponent;
import net.dv8tion.jda.api.components.container.Container;
import net.dv8tion.jda.api.components.separator.Separator;
import net.dv8tion.jda.api.components.textdisplay.TextDisplay;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.nwtech.qtty.application.port.out.DiscordGateway;
import net.nwtech.qtty.application.port.out.GuildRepositoryPort;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AskSetUpUseCase {

    private final Logger LOGGER = LoggerFactory.getLogger(AskSetUpUseCase.class);

    public void execute(@NotNull GuildJoinEvent event) {
        var guild = event.getGuild();
        TextChannel channel = null;
        if (guild.getSystemChannel() != null)
            channel = guild.getSystemChannel();
        else
            if (guild.getDefaultChannel() != null)
                channel = guild.getDefaultChannel().asTextChannel();

        if (channel == null) {
            LOGGER.warn("Guild {}[{}] has no System or Default Channel", event.getGuild().getName(), guild.getId());
            sendMessageToOwner(guild);
            return;
        }
        channel.sendMessageComponents(buildSetupMessageWarn(guild)).useComponentsV2().queue();
    }

    private void sendMessageToOwner(Guild guild) {
        var owner = guild.getOwner();
        if (owner == null) {
            LOGGER.warn("Guild {}[{}] has no Owner", guild.getName(), guild.getId());
            return;
        }
        owner.getUser().openPrivateChannel().queue(channel -> {
            channel.sendMessageComponents(buildSetupMessageWarn(guild)).useComponentsV2().queue();
        });
    }

    private MessageTopLevelComponent buildSetupMessageWarn(Guild guild) {
        return Container.of(
                TextDisplay.of("## Setup\n"),
                Separator.createDivider(Separator.Spacing.SMALL),
                TextDisplay.of("""
                    Hey! Looks like I'm new here at %s. To unlock all my features, please follow the steps below:
                    - Initialize your guild in our app with ``/init``
                    - Set a log channel to start logging and monitoring your server with: ``/set-audit-channel #text-channel``
                    - Set a movie channel to start rating and displaying your watched movies with: ``/set-movies-channel #text-channel``
                    """.formatted(guild.getName())
                ),
                Separator.create(true, Separator.Spacing.SMALL),
                TextDisplay.of("If you encounter any problems, bugs, or have any questions, contact my creator on Discord: @newtbtw")
        );
    }

}

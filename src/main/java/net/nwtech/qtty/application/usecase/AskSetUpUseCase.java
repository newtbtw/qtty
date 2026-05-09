package net.nwtech.qtty.application.usecase;

import net.dv8tion.jda.api.components.MessageTopLevelComponent;
import net.dv8tion.jda.api.components.container.Container;
import net.dv8tion.jda.api.components.separator.Separator;
import net.dv8tion.jda.api.components.textdisplay.TextDisplay;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AskSetUpUseCase {

    private static final Logger LOGGER = LoggerFactory.getLogger(AskSetUpUseCase.class);

    public void execute(@NotNull GuildJoinEvent event) {
        var guild = event.getGuild();
        var channel = resolveSetupChannel(guild);

        if (channel.isEmpty()) {
            LOGGER.warn("Guild {}[{}] has no System or Default Channel", event.getGuild().getName(), guild.getId());
            sendMessageToOwner(guild);
            return;
        }

        channel.get()
                .sendMessageComponents(buildSetupMessageWarn(guild))
                .useComponentsV2()
                .queue(
                        ignored -> LOGGER.info("Setup message sent to guild {}[{}]", guild.getName(), guild.getId()),
                        error -> {
                            LOGGER.warn("Failed to send setup message to guild {}[{}] channel {}. Falling back to owner DM.",
                                    guild.getName(), guild.getId(), channel.get().getId(), error);
                            sendMessageToOwner(guild);
                        }
                );
    }

    private void sendMessageToOwner(Guild guild) {
        var owner = guild.getOwner();
        if (owner == null) {
            LOGGER.warn("Guild {}[{}] has no Owner", guild.getName(), guild.getId());
            return;
        }

        owner.getUser().openPrivateChannel().queue(
                channel -> channel.sendMessageComponents(buildSetupMessageWarn(guild))
                        .useComponentsV2()
                        .queue(
                                ignored -> LOGGER.info("Setup message sent to owner of guild {}[{}]", guild.getName(), guild.getId()),
                                error -> LOGGER.warn("Failed to send setup message to owner of guild {}[{}]",
                                        guild.getName(), guild.getId(), error)
                        ),
                error -> LOGGER.warn("Failed to open private channel with owner of guild {}[{}]",
                        guild.getName(), guild.getId(), error)
        );
    }

    private Optional<TextChannel> resolveSetupChannel(Guild guild) {
        var systemChannel = guild.getSystemChannel();
        if (systemChannel != null && systemChannel.canTalk()) {
            return Optional.of(systemChannel);
        }

        var defaultChannel = guild.getDefaultChannel();
        if (defaultChannel instanceof TextChannel textChannel && textChannel.canTalk()) {
            return Optional.of(textChannel);
        }

        return Optional.empty();
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

package net.nwtech.qtty.discord.commands;

import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.nwtech.qtty.application.usecase.InitializeGuildUseCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InitCommand implements ISlashCommand {

    private final InitializeGuildUseCase initializeGuildUseCase;
    private static final Logger logger = LoggerFactory.getLogger(InitCommand.class);

    @Override
    public List<OptionData> getOptions() {
        return List.of();
    }

    @Override
    public String getName() {
        return "init";
    }

    @Override
    public String getDescription() {
        return "Create a initial setup for this guild in the bot's app.";
    }

    @Override
    public DefaultMemberPermissions getMemberPermissions() {
        return DefaultMemberPermissions.enabledFor(Permission.ADMINISTRATOR);
    }

    @Override
    public void onCommand(SlashCommandInteractionEvent event) {
        var eventGuild = event.getGuild();
        if (eventGuild == null) {
            event.reply("You are sending this command outside a guild!").setEphemeral(true).queue();
            logger.info("Command sent outside a guild!");
            return;
        }

        var result = initializeGuildUseCase.execute(eventGuild.getIdLong());
        logger.info("Guild {} initialized. members={}, usersCreated={}, rolesCreated={}, profilesUpserted={}",
                result.guild().discordId(),
                result.membersProcessed(),
                result.usersCreated(),
                result.rolesCreated(),
                result.profilesUpserted());

        event.reply("Guild initialized successfully. Members: %d, new users: %d, new roles: %d, profiles synced: %d"
                        .formatted(
                                result.membersProcessed(),
                                result.usersCreated(),
                                result.rolesCreated(),
                                result.profilesUpserted()
                        ))
                .setEphemeral(true)
                .queue();
    }
}

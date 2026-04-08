package net.nwtech.qtty.discord.commands;


import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.nwtech.qtty.application.usecase.UpdateGuildAuditChannelCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuditChannelCommand implements ISlashCommand {

    private final UpdateGuildAuditChannelCase updateGuildAuditChannelCase;
    private final String CHANNEL_OPTION_NAME = "text-channel";
    private static final Logger logger = LoggerFactory.getLogger(AuditChannelCommand.class);

    @Override
    public List<OptionData> getOptions() {
        return List.of(new OptionData(OptionType.CHANNEL, CHANNEL_OPTION_NAME, "Chosen channel.", true));
    }

    @Override
    public String getName() {
        return "audit-channel";
    }

    @Override
    public String getDescription() {
        return "Sets the channel assigned for audit logs.";
    }

    @Override
    public DefaultMemberPermissions getMemberPermissions() {
        return DefaultMemberPermissions.enabledFor(Permission.ADMINISTRATOR);
    }

    @Override
    public void onCommand(SlashCommandInteractionEvent event) {
        var guild = event.getGuild();
        if (guild == null) {
            event.reply("You must be in a guildModel to use this command!").setEphemeral(true).queue();
            return;
        }
        var guildId = event.getGuild().getIdLong();

        var option = event.getOption(CHANNEL_OPTION_NAME);
        if (option == null) {
            event.reply("You must select a channel.").setEphemeral(true).queue();
            return;
        }
        var channel = option.getType() ==  OptionType.CHANNEL ? option.getAsChannel() : null;
        if (channel == null || channel.getType() != ChannelType.TEXT) {
            event.reply("You must select a valid voice channel.").setEphemeral(true).queue();
            return;
        }
        updateGuildAuditChannelCase.execute(guildId, channel.getIdLong());
    }
}

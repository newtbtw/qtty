package net.nwtech.qtty.discord.commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.List;

public interface ISlashCommand {

    List<OptionData> getOptions();
    String getName();
    String getDescription();
    DefaultMemberPermissions getMemberPermissions();

     void onCommand(SlashCommandInteractionEvent event);

     default CommandData getData(){
        return Commands.slash(getName(), getDescription()).addOptions(getOptions()).setDefaultPermissions(getMemberPermissions());
    }
}

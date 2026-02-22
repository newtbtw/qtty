package net.nwtech.qtty.discord.commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.List;


public abstract class SlashCommandTemplate {

    protected abstract List<OptionData> getOptions();
    protected abstract String getName();
    protected abstract String getDescription();
    protected abstract DefaultMemberPermissions getMemberPermissions();

    public abstract void onCommand(SlashCommandInteractionEvent event);

    public final CommandData getData(){
        return Commands.slash(getName(), getDescription()).addOptions(getOptions()).setDefaultPermissions(getMemberPermissions());
    }
}

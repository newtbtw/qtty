package net.nwtech.qtty.discord.listeners;

import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.nwtech.qtty.discord.commands.ISlashCommand;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;

@Component
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class SlashCommandListener extends ListenerAdapter {

    private final List<ISlashCommand> commands;
    private final Logger logger =  LoggerFactory.getLogger(this.getClass());

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        logger.info("User {} performed command {}", event.getUser().getEffectiveName(), event.getName());
        var optionalCommand = commands.stream()
                .filter(c -> c.getName().equals(event.getName()))
                .findFirst();
        if (optionalCommand.isPresent())
            optionalCommand.get().onCommand(event);
        else
            event.reply("No command found").queue();
    }
}
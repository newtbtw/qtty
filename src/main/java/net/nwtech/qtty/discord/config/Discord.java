package net.nwtech.qtty.discord.config;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.nwtech.qtty.discord.commands.ISlashCommand;
import net.nwtech.qtty.discord.listeners.IListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.Assert;

import java.util.HashMap;
import java.util.List;

@Configuration
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class Discord {

    private final Logger logger =  LoggerFactory.getLogger(this.getClass());
    private final List<ISlashCommand> commands;
    private final List<IListener> listeners;
    private final JDA JDA_CLIENT;

    @Value("${DELETE_OLD_CMD}")
    private boolean deleteOldCommands;

    @PostConstruct
    public void init() {
        logger.info("Initializing Discord");
        logger.info("Upserting listeners");
        upsertListeners(listeners);
        logger.info("Upserting commands");
        upsertCommands(commands);
        logger.info("Deleting old commands: {}", deleteOldCommands);
        if (deleteOldCommands)
            deleteOldCommands();
    }

    private void deleteOldCommands() {
        HashMap<String, ISlashCommand> commandMap = new HashMap<>();

        commands.forEach(command -> commandMap.put(command.getName(), command));

        for (Guild guild : JDA_CLIENT.getGuilds()) {
            guild.retrieveCommands().queue(commands -> {
                for (var cmd  : commands) {
                    logger.info("Analyzing: {}", cmd.getName());
                    if (!commandMap.containsKey(cmd.getName())) {
                        cmd.delete().queue();
                        logger.info("Command - {} - has been deleted.", cmd.getName());
                    }
                    logger.info("No need for deletion");
                }
            });
        }
    }

    private void upsertCommands(List<ISlashCommand> commands) {
        commands.forEach(command -> {
            JDA_CLIENT.upsertCommand(command.getData()).queue(s -> {
                logger.info("Upserted command: {}", s.getName());
            });;
        });
    }

    private void upsertListeners(List<IListener> listeners) {
        listeners.forEach(listener -> {
            JDA_CLIENT.addEventListener(listener);
            logger.info("Added listener: {}", listener.getClass().getName());
        });
    }

}

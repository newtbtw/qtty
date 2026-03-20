package net.nwtech.qtty.discord.config;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.nwtech.qtty.discord.commands.ISlashCommand;
import net.nwtech.qtty.discord.listeners.IListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.Assert;

import java.util.List;

@Configuration
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class Discord {

    public volatile static JDA JDA_CLIENT;

    private final Logger logger =  LoggerFactory.getLogger(this.getClass());
    private final List<ISlashCommand> commands;
    private final List<IListener> listeners;
    @Value("${DISCORD_TOKEN}") private String discordToken;

    @PostConstruct
    public void init() {
        Assert.notNull(discordToken, "Discord token is null");
        JDA_CLIENT = JDABuilder
                .createDefault(discordToken)
                .build();
        upsertCommands(commands);
    }

    public void upsertCommands(List<ISlashCommand> commands) {
        commands.forEach(command -> {
            JDA_CLIENT.upsertCommand(command.getData()).queue(s -> {
                logger.info("Upserted command: {}", s.getName());
            });;
        });
    }

}

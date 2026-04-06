package net.nwtech.qtty.config;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DiscordConfig {

    @Value("${DISCORD_TOKEN}") private String discordToken;

    @Bean
    @Qualifier("jda")
    public JDA jda() {
        return  JDABuilder
                .createDefault(discordToken)
                .setAutoReconnect(true)
                .build();
    }
}

package net.nwtech.qtty.config;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class DiscordConfig {

    @Value("${DISCORD_TOKEN}") private String discordToken;

    @Bean
    @Qualifier("jda")
    public JDA jda() {
        return  JDABuilder
                .createDefault(discordToken)
                .setAutoReconnect(true)
                .setEnabledIntents(List.of(
                        GatewayIntent.AUTO_MODERATION_CONFIGURATION,
                        GatewayIntent.GUILD_EXPRESSIONS,
                        GatewayIntent.GUILD_MESSAGES,
                        GatewayIntent.GUILD_PRESENCES,
                        GatewayIntent.GUILD_MESSAGE_REACTIONS,
                        GatewayIntent.GUILD_MEMBERS,
                        GatewayIntent.GUILD_VOICE_STATES,
                        GatewayIntent.SCHEDULED_EVENTS
                        ))
                .setMemberCachePolicy(MemberCachePolicy.ALL)
                .build();
    }
}

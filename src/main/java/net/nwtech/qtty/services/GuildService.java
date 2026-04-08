package net.nwtech.qtty.services;

import lombok.RequiredArgsConstructor;
import net.nwtech.qtty.discord.services.DiscordService;
import net.nwtech.qtty.entity.Guild;
import net.nwtech.qtty.repositories.GuildRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;

@Service
@EnableAsync
@RequiredArgsConstructor
public class GuildService {

    private final GuildRepository guildRepository;
    private final DiscordService discordService;
    private final Logger logger =  LoggerFactory.getLogger(this.getClass());

    public Guild createGuild(@NotNull long guildDiscordId) {
        logger.info("Creating guildModel with id {}", guildDiscordId);
        var discordGuild = discordService.getGuild(guildDiscordId);
        if (discordGuild.isEmpty()) {
            throw new IllegalArgumentException("GuildModel not found");
        }
        var name = discordGuild.get().getName();
        var allowed = false;
        var discordId = discordGuild.get().getIdLong();

        return guildRepository.save(new Guild(discordId, allowed, name));
    }

    public boolean guildExists(long guildId) {
        return guildRepository.existsGuildByDiscordId(guildId);
    }

    public Guild getGuild(long id) {
        return guildRepository.findById(id).orElse(null);
    }

    public Guild getOrCreate(long guildId) {
        return guildRepository.findByDiscordId(guildId).orElse(createGuild(guildId));
    }
}

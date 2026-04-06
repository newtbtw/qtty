package net.nwtech.qtty.application.port.out;

import java.util.List;
import java.util.Optional;

public interface DiscordGateway {

    Optional<DiscordGuild> findGuildById(long guildId);

    List<DiscordMember> getGuildMembers(long guildId);

    Optional<DiscordMember> findGuildMember(long guildId, long userId);

    record DiscordGuild(long discordId, String name) {
    }

    record DiscordRole(long discordId, String name, long guildDiscordId) {
    }

    record DiscordMember(long discordId, String username, String nickname, List<DiscordRole> roles) {
    }
}

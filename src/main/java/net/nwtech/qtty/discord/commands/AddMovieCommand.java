package net.nwtech.qtty.discord.commands;

import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AddMovieCommand implements ISlashCommand{

    private final String BANNER = "banner";
    private final String BANNER_URL = "banner-url";
    private final String MOVIE_TITLE = "movie-title";

    @Override
    public List<OptionData> getOptions() {
        return List.of(new OptionData(OptionType.ATTACHMENT, BANNER, "Movie image", false),
                new OptionData(OptionType.STRING, BANNER_URL, "Movie image", false),
                new OptionData(OptionType.STRING, MOVIE_TITLE, "Movie name", true)
                );
    }

    @Override
    public String getName() {
        return "add-movie";
    }

    @Override
    public String getDescription() {
        return "";
    }

    @Override
    public DefaultMemberPermissions getMemberPermissions() {
        return DefaultMemberPermissions.enabledFor(Permission.EMPTY_PERMISSIONS);
    }

    @Override
    public void onCommand(SlashCommandInteractionEvent event) {
        var imgOption = getImageOption(event);
        var movieTitleOption = event.getOption(BANNER);
        String movieTitle = movieTitleOption == null ? "" : movieTitleOption.getAsString();
        if (imgOption == null) {
            event.reply("Please insert an image to add this movie!").setEphemeral(true).queue();
            return;
        }
        if (movieTitle.isEmpty()) {
            event.reply("Please insert a title to add this movie!").setEphemeral(true).queue();
            return;
        }

    }

    private OptionMapping getImageOption(SlashCommandInteractionEvent event) {
        var attachOptions = event.getOptionsByType(OptionType.ATTACHMENT);
        if (!attachOptions.isEmpty()) {
            return attachOptions.getFirst();
        }
        return  event.getOption(BANNER_URL);
    }
}

package net.nwtech.qtty.discord.commands;

import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.components.MessageTopLevelComponent;
import net.dv8tion.jda.api.components.actionrow.ActionRow;
import net.dv8tion.jda.api.components.buttons.Button;
import net.dv8tion.jda.api.components.container.Container;
import net.dv8tion.jda.api.components.container.ContainerChildComponent;
import net.dv8tion.jda.api.components.mediagallery.MediaGallery;
import net.dv8tion.jda.api.components.mediagallery.MediaGalleryItem;
import net.dv8tion.jda.api.components.textdisplay.TextDisplay;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.utils.FileUpload;
import net.nwtech.qtty.adapters.out.persistence.MoviePersistenceAdapter;
import net.nwtech.qtty.adapters.out.storage.S3ImageStorageAdapter;
import net.nwtech.qtty.adapters.out.tmdb.TmdbAdapter;
import net.nwtech.qtty.domain.model.MovieModel;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AddMovieCommand implements ISlashCommand{

    private final String MOVIE_TITLE = "movie-title";
    private final TmdbAdapter tmdb;
    private final MoviePersistenceAdapter movieAdapter;
    private final S3ImageStorageAdapter s3ImageStorage;

    @Override
    public List<OptionData> getOptions() {
        return List.of(
                new OptionData(OptionType.STRING, MOVIE_TITLE, "Movie's title", true)
                );
    }

    @Override
    public String getName() {
        return "add-movie";
    }

    @Override
    public String getDescription() {
        return "Adds a movie to watched movies channel!";
    }

    @Override
    public DefaultMemberPermissions getMemberPermissions() {
        return DefaultMemberPermissions.enabledFor(Permission.EMPTY_PERMISSIONS);
    }

    @Override
    public void onCommand(SlashCommandInteractionEvent event) {
        event.deferReply(true).queue();
        var movieTitleOption = event.getOption(MOVIE_TITLE);
        String movieTitle = movieTitleOption == null ? "" : movieTitleOption.getAsString();
        if (movieTitle.isEmpty()) {
            event.reply("Please insert a title to add this movie!").setEphemeral(true).queue();
            return;
        }

        var tmdbMovies = tmdb.searchMoviesFromTitle(movieTitle);
        if (tmdbMovies.isEmpty()){
            event.reply("No movies found for " + movieTitle).setEphemeral(true).queue();
            return;
        }

        var movies = movieAdapter.saveAll(tmdbMovies);

        event.getHook().editOriginalComponents(buildMovieOptionsComponents(movies)).useComponentsV2().queue();
    }

    private List<MessageTopLevelComponent> buildMovieOptionsComponents(List<MovieModel> movies) {
       List<MessageTopLevelComponent> containers = new ArrayList<>();
       for (var m : movies) {
           List<ContainerChildComponent> content = new ArrayList<>();
           content.add(TextDisplay.of("## " + m.title()));
           content.add(TextDisplay.of("### Sinopse:\n" + m.synopsis()));
           if (m.imagePath() != null && !m.imagePath().isBlank()) {
               var movieImgByte = s3ImageStorage.fetch(m.imagePath());
               content.add(MediaGallery.of(MediaGalleryItem.fromFile(FileUpload.fromData(movieImgByte, m.imagePath().replace("movies/", "")))));
           }
           content.add(ActionRow.of(Button.success("pub-" + m.id(), "Publish")));
           var c = Container.of(content);
           containers.add(c);
       }
       return containers;
    }
}

package net.nwtech.qtty.application.usecase;

import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.components.MessageTopLevelComponent;
import net.dv8tion.jda.api.components.actionrow.ActionRow;
import net.dv8tion.jda.api.components.buttons.Button;
import net.dv8tion.jda.api.components.container.Container;
import net.dv8tion.jda.api.components.mediagallery.MediaGallery;
import net.dv8tion.jda.api.components.mediagallery.MediaGalleryItem;
import net.dv8tion.jda.api.components.separator.Separator;
import net.dv8tion.jda.api.components.textdisplay.TextDisplay;
import net.dv8tion.jda.api.components.thumbnail.Thumbnail;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.utils.FileUpload;
import net.nwtech.qtty.adapters.out.persistence.GuildPersistenceAdapter;
import net.nwtech.qtty.adapters.out.persistence.MoviePersistenceAdapter;
import net.nwtech.qtty.adapters.out.storage.S3ImageStorageAdapter;
import net.nwtech.qtty.domain.model.MovieModel;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PublishMovieUseCase {

    private final MoviePersistenceAdapter movieAdapter;
    private final GuildPersistenceAdapter guildAdapter;
    private final S3ImageStorageAdapter s3ImageStorage;
    private final EnsureGuildUseCase ensureGuildUseCase;

    public void execute(ButtonInteractionEvent event, String movieId){
        event.deferReply().setEphemeral(true).queue();
        var guild = event.getGuild();
        if (guild == null){
            event.reply("You must to be in a guild!").setEphemeral(true).queue(); // will never hit
            return;
        }
        var guildId = guild.getIdLong();
        long id = 0L;
        try {
            id = Long.parseLong(movieId);
        } catch (NumberFormatException e) {
            event.getHook().editOriginal("Invalid movie id").queue();
            return;
        }

        var movie = movieAdapter.findById(id);
        if (movie.isEmpty()) {
            event.reply("Movie not found in DB.").setEphemeral(true).queue();
            return;
        }

        var movieModel = movie.get();
        var guildModel = guildAdapter.findByDiscordId(guildId).orElse(ensureGuildUseCase.execute(guildId));
        var channelId = guildModel.moviesChannelId();

        var movieChannel = event.getGuild().getTextChannelById(channelId);
        if (movieChannel == null) {
            event.reply("The movie channel config is outdated, channel no longer exists!").setEphemeral(true).queue();
            return;
        }

        List<MessageTopLevelComponent> components = null;
        try {
            components = buildMovieMessage(movieModel);
        } catch (IOException e) {
           event.getHook().editOriginal("Error building movie message! Try again later.").queue();
           return;
        }

        movieChannel.sendMessageComponents(components).useComponentsV2().queue(message -> movieAdapter.updateMovieMessageId(movieModel.id(), message.getIdLong()));
    }

    private List<MessageTopLevelComponent> buildMovieMessage(MovieModel movieModel) throws IOException {
        List<MessageTopLevelComponent> components = new ArrayList<>();

        var poster = s3ImageStorage.fetch(movieModel.imagePath());
        FileUpload fileUpload = FileUpload.fromData(poster, movieModel.title() + ".png");

        StringBuilder sb = new StringBuilder();
        sb.append("**").append("Nota: ").append(movieModel.getCurrentRating()).append("**").append("\n");
        sb.append("\n");
        sb.append("Data de Lançamento: ").append(movieModel.getFormattedReleaseDate());
        sb.append("\n## Sinopse: \n");
        sb.append(movieModel.synopsis());
        var mainDisplay = Container.of(
                TextDisplay.of("# " + movieModel.title()),
                Separator.create(true, Separator.Spacing.SMALL),
                TextDisplay.of(sb.toString()),
                MediaGallery.of(MediaGalleryItem.fromFile(fileUpload)),
                Separator.createInvisible(Separator.Spacing.SMALL),
                TextDisplay.of("Movies and posters from The Movie Database at: https://www.themoviedb.org"),
                ActionRow.of(Button.success("vote-" + movieModel.id(), "Votar"))
        );
        components.add(mainDisplay);
        return components;
    };

}

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
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.utils.FileUpload;
import net.nwtech.qtty.adapters.out.persistence.MoviePersistenceAdapter;
import net.nwtech.qtty.adapters.out.storage.S3ImageStorageAdapter;
import net.nwtech.qtty.application.requests.VoteRequest;
import net.nwtech.qtty.domain.model.MovieModel;
import net.nwtech.qtty.domain.model.RatingRecordModel;
import net.nwtech.qtty.domain.model.UserModel;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class VoteMovieUseCase {

    private final MoviePersistenceAdapter movieAdapter;
    private final S3ImageStorageAdapter s3ImageStorage;

    public void execute(InteractionHook hook, VoteRequest vt){
        long id = vt.movieId();

        var movie = movieAdapter.findById(id);
        if (movie.isEmpty()) {
            hook.editOriginal("Movie not found in DB.").queue();
            return;
        }

        var movieModel = movie.get();
        if (movieModel.discordRatingMessageId() == null) {
            hook.editOriginal("Movie rating message not found. Publish the movie again.").queue();
            return;
        }

        var updatedMovie = movieAdapter.updateRating(movieModel, addRating(movieModel.ratings(), movieModel, vt.rating(), vt.voter()));
        var channel = hook.getInteraction().getMessageChannel();
        if (channel == null) {
            hook.editOriginal("Movie channel not available.").queue();
            return;
        }

        channel.retrieveMessageById(updatedMovie.discordRatingMessageId()).queue(message -> {
            List<MessageTopLevelComponent> components;
            if (message.getAttachments().isEmpty()) {
                components = buildMovieMessage(updatedMovie, null);
            } else {
                components = buildMovieMessage(updatedMovie, message.getAttachments().getFirst().getUrl());
            }

            message.editMessageComponents(components)
                    .useComponentsV2()
                    .queue(
                            ignored -> hook.editOriginal("Movie successfully updated!").queue(),
                            error -> hook.editOriginal("Error updating movie message! Try again later.").queue()
                    );
        }, error -> hook.editOriginal("Movie message not found in channel.").queue());
    }

    private List<RatingRecordModel> addRating(List<RatingRecordModel> ratings, MovieModel movie, double rating, UserModel user) {
        List<RatingRecordModel> updatedRatings = new ArrayList<>(ratings);
        for (int i = 0; i < updatedRatings.size(); i++) {
            RatingRecordModel existingRating = updatedRatings.get(i);
            if (existingRating.user() != null && user != null && existingRating.user().id().equals(user.id())) {
                updatedRatings.set(i, RatingRecordModel.builder()
                        .id(existingRating.id())
                        .user(user)
                        .movie(movie)
                        .rating(rating)
                        .build());
                return updatedRatings;
            }
        }

        updatedRatings.add(RatingRecordModel.builder()
                .user(user)
                .movie(movie)
                .rating(rating)
                .build());
        return updatedRatings;
    }

    private List<MessageTopLevelComponent> buildMovieMessage(MovieModel movieModel, String discordCDNImageUrl) {
        List<MessageTopLevelComponent> components = new ArrayList<>();

        StringBuilder sb = new StringBuilder();
        sb.append("**").append("Nota: ").append( movieModel.getCurrentRating()).append("**").append("\n");
        sb.append("\n");
        sb.append("Data de Lançamento: ").append(movieModel.getFormattedReleaseDate());
        sb.append("\n## Sinopse: \n");
        sb.append(movieModel.synopsis());

        var mediaGalery = discordCDNImageUrl == null
                ? MediaGallery.of(MediaGalleryItem.fromFile(getImage(movieModel)))
                : MediaGallery.of(MediaGalleryItem.fromUrl(discordCDNImageUrl));

        var mainDisplay = Container.of(
                TextDisplay.of("# " + movieModel.title()),
                Separator.create(true, Separator.Spacing.SMALL),
                TextDisplay.of(sb.toString()),
                mediaGalery,
                Separator.createInvisible(Separator.Spacing.SMALL),
                TextDisplay.of("Votos: \n" + movieModel.getVotersMentions()),
                Separator.createInvisible(Separator.Spacing.SMALL),
                TextDisplay.of("Movies and posters from The Movie Database at: https://www.themoviedb.org"),
                ActionRow.of(Button.success("vote-" + movieModel.id(), "Votar"))
        );
        components.add(mainDisplay);
        return components;
    };

    private FileUpload getImage(MovieModel movieModel) {
        var poster = s3ImageStorage.fetch(movieModel.imagePath());
        return FileUpload.fromData(poster, movieModel.title() + ".png");
    }

}

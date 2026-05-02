package net.nwtech.qtty.discord.listeners;

import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.nwtech.qtty.adapters.out.persistence.UserPersistenceAdapter;
import net.nwtech.qtty.application.requests.VoteRequest;
import net.nwtech.qtty.application.usecase.VoteMovieUseCase;
import net.nwtech.qtty.domain.model.UserModel;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ModalInteractionListener extends ListenerAdapter implements IListener{

    private final VoteMovieUseCase voteCase;
    private final UserPersistenceAdapter userAdapter;

    @Override
    public void onModalInteraction(@NotNull ModalInteractionEvent event) {
        event.deferReply().setEphemeral(true).queue();
        var modalId = event.getModalId();
        if (modalId.startsWith("vote-")){
            var vt = buildVoteRequest(event);
            if (vt == null) {
                event.getHook().editOriginal("Vote request failed!").queue();
                return;
            };
            voteCase.execute(event.getHook(), vt);
        }
    }

    private VoteRequest buildVoteRequest(ModalInteractionEvent event) {
        String[] movieId = event.getModalId().split("-");
        if (movieId.length != 2){
            event.getHook().editOriginal("Invalid movie id!").queue();
            return null;
        }
        int id = 0;
        try {
            id =  Integer.parseInt(movieId[1]);
        } catch (NumberFormatException e) {
            event.getHook().editOriginal("Invalid movie id!").queue();
            return null;
        }
        double rating;
        try {
            var input = event.getValue("rating");
            if (input == null) return null;
            String sValue = input.getAsString().replace(",",".");
            rating = Double.parseDouble(sValue);
        } catch (NumberFormatException e) {
            event.getHook().editOriginal("Invalid rating!").queue();
            return null;
        }

        if (rating > 10) {
            event.getHook().editOriginal("Invalid rating! Max value: 10.00").queue();
            return null;
        }

        var member = event.getMember();
        if (member == null) return null;
        var userId = member.getUser().getIdLong();

        var voter = userAdapter.findByDiscordId(userId).orElse(userAdapter.save(new UserModel(
                null,
                userId,
                member.getUser().getGlobalName()
        )));

        return VoteRequest.
                builder()
                .voter(voter)
                .movieId(id)
                .rating(rating)
                .build();
    }
}

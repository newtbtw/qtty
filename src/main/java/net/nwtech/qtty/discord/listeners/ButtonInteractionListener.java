package net.nwtech.qtty.discord.listeners;

import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.components.label.Label;
import net.dv8tion.jda.api.components.textinput.TextInput;
import net.dv8tion.jda.api.components.textinput.TextInputStyle;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.modals.Modal;
import net.nwtech.qtty.application.usecase.PublishMovieUseCase;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ButtonInteractionListener extends ListenerAdapter implements IListener {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final PublishMovieUseCase publishCase;

    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
        logger.info("User {} interacted with button {}", event.getUser().getName(), event.getButton());
        String buttonId = event.getComponent().getCustomId();

        if (buttonId == null) {
            event.reply("Null button id").setEphemeral(true).queue();
            return;
        }
        if (buttonId.startsWith("pub-")) {
            var movieId =  buttonId.split("-")[1];
            logger.info("Movie publish button interaction - movie id: {}", buttonId);
            publishCase.execute(event, movieId);
            return;
        }
        if (buttonId.startsWith("vote-")) {
            var movieId =  buttonId.split("-")[1];
            logger.info("Movie vote button interaction - movie id: {}", buttonId);
            event.replyModal(ratingModal(buttonId)).queue();
        }
    }

    private Modal ratingModal(String modalId) {
        TextInput ratingField = TextInput.create("rating", TextInputStyle.SHORT)
                .setPlaceholder("Seu voto em formato decimal")
                .setMinLength(1)
                .setMaxLength(4)
                .build();
        return Modal.create(modalId, "Vote!")
                .addComponents(
                        Label.of("Seu voto: ", ratingField)
                ).build();
    }

}

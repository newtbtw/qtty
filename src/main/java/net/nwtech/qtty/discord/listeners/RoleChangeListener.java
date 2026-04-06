package net.nwtech.qtty.discord.listeners;

import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.events.role.RoleCreateEvent;
import net.dv8tion.jda.api.events.role.RoleDeleteEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.nwtech.qtty.application.usecase.DeleteRoleUseCase;
import net.nwtech.qtty.application.usecase.SyncRoleCreatedUseCase;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RoleChangeListener extends ListenerAdapter implements IListener {

    private final Logger logger = LoggerFactory.getLogger(RoleChangeListener.class);
    private final SyncRoleCreatedUseCase syncRoleCreatedUseCase;
    private final DeleteRoleUseCase deleteRoleUseCase;

    @Override
    public void onRoleCreate(@NotNull RoleCreateEvent event) {
        syncRoleCreatedUseCase.execute(
                event.getGuild().getIdLong(),
                event.getRole().getIdLong(),
                event.getRole().getName()
        );
        logger.info("Role {} synced from guild {}.", event.getRole().getName(), event.getGuild().getIdLong());
    }

    @Override
    public void onRoleDelete(@NotNull RoleDeleteEvent event) {
        deleteRoleUseCase.execute(event.getRole().getIdLong());
        logger.info("Role {} has been deleted from db.", event.getRole().getName());
    }
}

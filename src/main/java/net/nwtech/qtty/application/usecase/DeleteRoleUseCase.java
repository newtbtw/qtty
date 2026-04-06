package net.nwtech.qtty.application.usecase;

import lombok.RequiredArgsConstructor;
import net.nwtech.qtty.application.port.out.RoleRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DeleteRoleUseCase {

    private final RoleRepositoryPort roleRepository;

    @Transactional
    public void execute(long roleDiscordId) {
        roleRepository.deleteByDiscordId(roleDiscordId);
    }
}

package net.nwtech.qtty.adapters.out.persistence;

import lombok.RequiredArgsConstructor;
import net.nwtech.qtty.application.port.out.UserRepositoryPort;
import net.nwtech.qtty.domain.model.UserModel;
import net.nwtech.qtty.repositories.UserRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UserPersistenceAdapter implements UserRepositoryPort {

    private final UserRepository userRepository;

    @Override
    public Optional<UserModel> findByDiscordId(long discordId) {
        return userRepository.findByDiscordId(discordId).map(this::toDomain);
    }

    @Override
    @Transactional
    public UserModel save(UserModel userModel) {
        net.nwtech.qtty.entity.User entity = resolveEntity(userModel);

        entity.setDiscordId(userModel.discordId());
        entity.setUsername(userModel.username());

        try {
            return toDomain(userRepository.saveAndFlush(entity));
        } catch (DataIntegrityViolationException ex) {
            return userRepository.findByDiscordId(userModel.discordId())
                    .map(existing -> {
                        existing.setUsername(userModel.username());
                        return toDomain(userRepository.save(existing));
                    })
                    .orElseThrow(() -> ex);
        }
    }

    private UserModel toDomain(net.nwtech.qtty.entity.User entity) {
        return new UserModel(entity.getId(), entity.getDiscordId(), entity.getUsername());
    }

    private net.nwtech.qtty.entity.User resolveEntity(UserModel userModel) {
        if (userModel.id() != null) {
            return userRepository.findById(userModel.id()).orElseGet(net.nwtech.qtty.entity.User::new);
        }
        return userRepository.findByDiscordId(userModel.discordId()).orElseGet(net.nwtech.qtty.entity.User::new);
    }
}

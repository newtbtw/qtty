package net.nwtech.qtty.adapters.out.persistence;

import lombok.RequiredArgsConstructor;
import net.nwtech.qtty.application.port.out.UserRepositoryPort;
import net.nwtech.qtty.domain.model.UserModel;
import net.nwtech.qtty.repositories.UserRepository;
import org.springframework.stereotype.Component;

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
    public UserModel save(UserModel userModel) {
        net.nwtech.qtty.entity.User entity = userModel.id() != null
                ? userRepository.findById(userModel.id()).orElse(new net.nwtech.qtty.entity.User())
                : new net.nwtech.qtty.entity.User();

        entity.setDiscordId(userModel.discordId());
        entity.setUsername(userModel.username());

        return toDomain(userRepository.save(entity));
    }

    private UserModel toDomain(net.nwtech.qtty.entity.User entity) {
        return new UserModel(entity.getId(), entity.getDiscordId(), entity.getUsername());
    }
}

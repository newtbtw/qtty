package net.nwtech.qtty.adapters.out.persistence;

import lombok.RequiredArgsConstructor;
import net.nwtech.qtty.application.port.out.UserRepositoryPort;
import net.nwtech.qtty.domain.model.User;
import net.nwtech.qtty.repositories.UserRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UserPersistenceAdapter implements UserRepositoryPort {

    private final UserRepository userRepository;

    @Override
    public Optional<User> findByDiscordId(long discordId) {
        return userRepository.findByDiscordId(discordId).map(this::toDomain);
    }

    @Override
    public User save(User user) {
        net.nwtech.qtty.entity.User entity = user.id() != null
                ? userRepository.findById(user.id()).orElse(new net.nwtech.qtty.entity.User())
                : new net.nwtech.qtty.entity.User();

        entity.setDiscordId(user.discordId());
        entity.setUsername(user.username());

        return toDomain(userRepository.save(entity));
    }

    private User toDomain(net.nwtech.qtty.entity.User entity) {
        return new User(entity.getId(), entity.getDiscordId(), entity.getUsername());
    }
}

package net.nwtech.qtty.services;

import lombok.RequiredArgsConstructor;
import net.nwtech.qtty.entity.UserGuildProfile;
import net.nwtech.qtty.repositories.UserGuildProfileRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserGuildProfileService {

    private final UserGuildProfileRepository userGuildProfileRepository;

    public void saveAll(List<UserGuildProfile> userGuildProfiles) {
        userGuildProfileRepository.saveAll(userGuildProfiles);
    }
}

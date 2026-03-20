package net.nwtech.qtty.services;

import lombok.RequiredArgsConstructor;
import net.nwtech.qtty.repositories.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class RoleService {

    private final RoleRepository roleRepository;

}

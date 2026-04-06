package net.nwtech.qtty.services;

import lombok.RequiredArgsConstructor;
import net.nwtech.qtty.entity.Role;
import net.nwtech.qtty.repositories.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class RoleService {

    private final RoleRepository roleRepository;
    private final GuildService guildService;

    public void saveAll(List<Role> rolesList) {
        roleRepository.saveAll(rolesList);
    }

    public void delete(long id) {
        roleRepository.deleteById(id);
    }

    public List<Role> getOrCreateRoles(List<net.dv8tion.jda.api.entities.Role> memberRoles) {
        final List<Role> roles = new ArrayList<>();
        memberRoles.forEach(role -> {
            roles.add(getOrCreateRole(role));
        });
        return roles;
    }

    private Role getOrCreateRole(net.dv8tion.jda.api.entities.Role role) {
        return roleRepository.findById(role.getIdLong()).orElse(roleRepository.save(new  Role(role.getIdLong(), role.getName(), guildService.getOrCreate(role.getGuild().getIdLong()))));
    }


}

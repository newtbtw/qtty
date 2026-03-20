package net.nwtech.qtty.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true,  nullable = false)
    private Long discordId;

    @Column(unique = true,  nullable = false)
    private String roleName;

    @ManyToOne
    @JoinColumn(name = "guild_id")
    private Guild guild;

    public Role(Long discordId, String roleName){
        this.discordId = discordId;
        this.roleName = roleName;
    }
}

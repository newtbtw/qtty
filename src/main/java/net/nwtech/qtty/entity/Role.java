package net.nwtech.qtty.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "dc_role")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true,  nullable = false)
    private Long discordId;

    @Column(nullable = false)
    private String roleName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "guild_id", nullable = false)
    private Guild guild;

    public Role(Long discordId, String roleName, Guild guild) {
        this.discordId = discordId;
        this.roleName = roleName;
        this.guild = guild;
    }
}

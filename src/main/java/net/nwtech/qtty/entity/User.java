package net.nwtech.qtty.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "qtty_user")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true,  nullable = false)
    private Long discordId;

    private String username;

    public User(Long discordId, String username) {
        this.discordId = discordId;
        this.username = username;
    }
}

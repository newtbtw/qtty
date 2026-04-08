package net.nwtech.qtty.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "dc_guild")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Guild {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true,  nullable = false)
    private Long discordId;

    private Boolean allowed;
    private String name;

    private Long auditChannelId;

    private Long moviesChannelId;

    public Guild(Long discordId, Boolean allowed, String name) {
        this.discordId = discordId;
        this.allowed = allowed;
        this.name = name;
    }

}

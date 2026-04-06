package net.nwtech.qtty.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Table(name = "dc_channel")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Channel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true,  nullable = false, updatable = false)
    private Long discordId;

    private String name;

    @Enumerated(EnumType.STRING)
    private ChannelType type;

    private enum ChannelType {
        VOICE,
        TEXT;
    }
}

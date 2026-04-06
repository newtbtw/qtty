package net.nwtech.qtty.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "dc_channel_permissions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChannelPermissions {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


}

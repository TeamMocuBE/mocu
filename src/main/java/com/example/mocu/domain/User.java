package com.example.mocu.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
public class User extends BaseEntity {
    @Id
    @GeneratedValue
    private Long id;

    @OneToMany(fetch = FetchType.LAZY)
    private List<Address> addresses;

    @NotEmpty
    private String name;

    private String provider;

    private String email;

    private String userImage;

    private String token;

    private Long deviceId;

    private String deviceToken;
}

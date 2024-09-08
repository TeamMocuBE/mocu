package com.example.mocu.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Entity
@Getter
@Setter
public class Address extends BaseEntity {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId")
    private User user;

    private String name;

    private String address;

    @NotEmpty
    private Double latitude;
    @NotEmpty
    private Double longitude;

    @PrePersist
    protected void onCreate() {
        if (Objects.equals(getStatus(), "active")) {
            setStatus("not select");
        }
    }
}

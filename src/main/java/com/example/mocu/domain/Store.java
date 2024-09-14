package com.example.mocu.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

@Entity
@Getter
@Setter
public class Store extends BaseEntity {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ownerId")
    private Owner owner;

    @NotEmpty
    private String name;

    @NotEmpty
    private String category;

    @NotEmpty
    private String address;

    @NotEmpty
    private Double latitude;
    @NotEmpty
    private Double longitude;

    private String mainImageUrl;

    @ColumnDefault("0.0")
    private Float rating;

    private String reward;

    private String event;

    private Integer maxStamp;
}

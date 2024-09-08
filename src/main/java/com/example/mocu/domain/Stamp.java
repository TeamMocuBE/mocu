package com.example.mocu.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

@Entity
@Getter
@Setter
public class Stamp extends BaseEntity {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "storeId")
    private Store store;

    @ColumnDefault("0")
    private Integer numOfStamp;

    @ColumnDefault("0")
    private Integer numOfCouponAvailable;

    @ColumnDefault("0")
    private Integer useCount;

    @ColumnDefault("false")
    private Boolean dueDate;
}

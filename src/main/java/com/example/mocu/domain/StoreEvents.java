package com.example.mocu.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Entity
@Getter
@Setter
public class StoreEvents extends BaseEntity {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "storeId")
    private Store store;

    private String content;

    private String startDate;
    private String endDate;

    @PrePersist
    protected void onCreate() {
        if (Objects.equals(getStatus(), "active")) {
            setStatus("in progress");
        }
    }
}

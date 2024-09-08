package com.example.mocu.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Entity
@Getter
@Setter
public class Mission extends BaseEntity {
    @Id
    @GeneratedValue
    private Long id;

    private String content;

    @PrePersist
    protected void onCreate() {
        if (Objects.equals(getStatus(), "active")) {
            setStatus("not-select");
        }
    }
}

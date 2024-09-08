package com.example.mocu.domain;

import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDateTime;

@MappedSuperclass
@Getter
@Setter
public class BaseEntity {
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;

    @ColumnDefault("active")
    private String status;
}

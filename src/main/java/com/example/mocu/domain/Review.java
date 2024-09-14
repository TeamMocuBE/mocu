package com.example.mocu.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.util.Objects;

@Entity
@Getter
@Setter
public class Review extends BaseEntity {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "storeId")
    private Store store;

    private Integer rate;

    private String content;

    @ColumnDefault("false")
    private Boolean report;

    @PrePersist
    protected void onCreate() {
        if (Objects.equals(getStatus(), "active")) {
            setStatus("작성 이전");
        }
    }
}

package com.example.mocu.Dto.store;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GetSearchedStoreResponse {
    private String name;
    private String reward;
    private String coordinate;
    private BigDecimal rating;
    private String numOfStamp;
}

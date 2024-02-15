package com.example.mocu.Dto.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GetRegularResponse {
    private String mainImageUrl;

    private String name;

    private int numOfStamp;

    private int maxStamp;

    private String reward;

    private int distance;
}

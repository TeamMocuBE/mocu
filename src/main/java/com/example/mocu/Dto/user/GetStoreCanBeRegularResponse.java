package com.example.mocu.Dto.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GetStoreCanBeRegularResponse {
    // storeId, 가게 대표 사진, 가게 상호명, 유저가 적립한 스탬프 수, 가게 maxStamp 수, reward, 가게 평점, distance
    private long storeId;
    private String mainImageUrl;
    private String storeName;
    private int numOfStamp;
    private int maxStamp;
    private String reward;
    private float rating;
    private double distance;
}

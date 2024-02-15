package com.example.mocu.Dto.stamp;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GetStampStoreAroundResponse {
    // 가게 대표 사진, 가게 이름, numOfStamp, maxStamp, rating, reward, numOfCouponAvailable, distance
    private String mainImageUrl;
    private String storeName;
    private int numOfStamp;
    private int maxStamp;
    private int numOfCouponAvailable;
    private String reward;
    private float rating;
    private double distance;
}

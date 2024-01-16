package com.example.mocu.Dto.store;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GetDetailedStoreResponse {
    /**
     * 가게 카테고리, 가게이름, 쿠폰 사용가능한 적립도장수, 쿠폰에 대한 보상, 가게 평점
     */
    private String category;
    private String storeName;
    private int maxStamp;
    private String reward;
    private float rating;
}

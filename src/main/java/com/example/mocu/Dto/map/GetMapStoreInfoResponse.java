package com.example.mocu.Dto.map;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GetMapStoreInfoResponse {
    // 가게 이름, 가게 대표 사진, 가게 카테고리, 쿠폰사용임박여부
    // 평점, 적립한 스탬프 수, maxStamp, 사용가능한 쿠폰수, 보상
    // 이벤트 여부
    private String storeName;
    private String mainImageUrl;
    private String category;
    private boolean dueDate;
    private float rating;
    private int numOfStamp;
    private int maxStamp;
    private int numOfCouponAvailable;
    private String reward;
    private boolean event;
}

package com.example.mocu.Dto.store;

import com.example.mocu.Dto.review.ReviewForUser;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GetDetailedStoreResponse {
    /**
     * 가게 이미지 List
     * 가게 카테고리, 가게이름, 내 스탬프 적립 현황, 내 쿠폰 현황, 쿠폰 사용가능한 적립도장수, 쿠폰에 대한 보상
     * 가게 평점, 유저별 리뷰와 평점
     */
    private List<String> storeImages;
    private String category;
    private String storeName;
    private int numOfStamp;
    private int numOfCouponAvailable;
    private int maxStamp;
    private String reward;
    private float rating;
    private List<ReviewForUser> reviews;
}

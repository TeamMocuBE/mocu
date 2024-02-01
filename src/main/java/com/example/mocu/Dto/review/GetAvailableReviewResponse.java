package com.example.mocu.Dto.review;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GetAvailableReviewResponse {
    private String storeImageUrl;

    //리뷰 테이블의 createdDate
    private String visitedDate;

    private String name;

    private String category;

    private int maxStamp;

    private int numOfStamp;

    private String reward;
}

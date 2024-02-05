package com.example.mocu.Dto.review;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GetMyReviewResponse {
    private String storeImageUrl;

    private String storeName;

    private int rate;

    private String createdDate;

    private String content;
}

package com.example.mocu.Dto.review;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PostReviewResponse {
    // 작성한 review의 reviewId
    // '리뷰 작성하기'가 오늘의 미션에 해당하는지를 check
    // missionComplete = false : '리뷰 작성하기'가 오늘의 미션 X
    //                 = true : '리뷰 작성하기'가 오늘의 미션 O
    private long reviewId;
    private boolean missionComplete;
}

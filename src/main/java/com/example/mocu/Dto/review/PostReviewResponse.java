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
    // '리뷰 작성하기' 가 오늘의 미션인지
    // '리뷰 작성하기' 를 수행함으로써 '미션 완료하기' 버튼을 활성화 시켜야 하는지
    private long reviewId;
    private boolean isTodayMission;
    private boolean isMissionComplete;
}

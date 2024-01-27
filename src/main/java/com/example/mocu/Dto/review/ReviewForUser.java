package com.example.mocu.Dto.review;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReviewForUser {
    // 평점, 리뷰내용, 최종 수정 시각
    private int rate;
    private int content;
    private String modifiedDate;
}

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
    // user 이름, user 프로필 이미지, 평점, 리뷰내용, 최종 수정 시각
    private String name;
    private String userImage;            // null 가능
    private int rate;
    private String content;
    private String modifiedDate;
}

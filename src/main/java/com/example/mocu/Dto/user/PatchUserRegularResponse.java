package com.example.mocu.Dto.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PatchUserRegularResponse {
    // regularId return
    // '단골 맺기'가 오늘의 미션인지 check
    // missionComplete = false : '리뷰 작성하기'가 오늘의 미션 X
    //                 = true : '리뷰 작성하기'가 오늘의 미션 O
    private long regularId;
    private boolean missionComplete;
}

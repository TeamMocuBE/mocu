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
    // regularId
    // '단골 맺기'가 오늘의 미션인지 check
    // missionComplete = false : '단골 맺기'가 오늘의 미션 X
    //                            or '단골 맺기'가 오늘의 미션이지만 단골을 맺지 않은 경우
    //                 = true : '단골 맺기'가 오늘의 미션 O
    private long regularId;
    private boolean missionComplete;
}

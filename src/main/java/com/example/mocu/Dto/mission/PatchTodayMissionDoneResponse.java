package com.example.mocu.Dto.mission;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PatchTodayMissionDoneResponse {
    // 미션 스탬프 적립 가능한 경우 : 수행한 오늘의 미션 content return
    // 미션 스탬프 적립 가능하지 않은 경우 : "이미 2개의 미션 스탬프를 획득하였습니다." return
    // 미션 스탬프 적립 후, 미션 맵 보상 받을 수 있는지 여부
    private String content;
    private boolean isPossibleGetReward;
}

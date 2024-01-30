package com.example.mocu.Dto.mission;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GetTodayMissionResponse {
    // 오늘의 미션으로 선택된 todayMissionId, 미션의 content, 해당 미션의 완료 여부
    // status가 'not-done' 이면 '미션 수행하기' 버튼으로 표시
    // status가 'done' 이면 '미션 완료하기' 버튼 활성화
    private long todayMissionId;
    private String content;
    private String status;
}

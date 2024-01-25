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
    // 오늘의 미션으로 선택된 미션의 content, 해당 미션의 수행 여부
    private String content;
    private String status;
}

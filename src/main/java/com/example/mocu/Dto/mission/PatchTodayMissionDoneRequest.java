package com.example.mocu.Dto.mission;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PatchTodayMissionDoneRequest {
    // status가 'done'인 todayMissionId, userId
    private long todayMissionId;
    private long userId;
}

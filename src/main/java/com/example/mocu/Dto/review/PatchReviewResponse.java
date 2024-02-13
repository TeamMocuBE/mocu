package com.example.mocu.Dto.review;

import com.example.mocu.Dto.mission.IsTodayMission;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PatchReviewResponse {
    // 오늘의 미션에 해당하는 리뷰 작성 기능 List return
    private List<IsTodayMission> todayMissionList;
}

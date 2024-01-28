package com.example.mocu.Controller;

import com.example.mocu.Common.response.BaseResponse;
import com.example.mocu.Dto.mission.GetTodayMissionResponse;
import com.example.mocu.Service.MissionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/mission")
public class MissionController {
    private final MissionService missionService;

    /**
     * 오늘의 미션 페이지 조회
     */
    @GetMapping("/{userId}/today-mission")
    public BaseResponse<List<GetTodayMissionResponse>> getTodayMissionsForUser(
            @PathVariable long userId){
        log.info("[MissionController.getTodayMissionsForUsers]");

        return new BaseResponse<>(missionService.getTodayMissionsForUser(userId));
    }

    /**
     * 오늘의 미션 update
     * 'MOCU앱 출석하기' 는 고정 미션
     * 추가로 2개가 랜덤으로 뽑혀짐
     */
    public BaseResponse<String> updateTodayMissions(){
        log.info("[MissionController.updateTodayMissions]");
        try {
            missionService.updateTodayMissions();
            return new BaseResponse<>("오늘의 미션 update 완료.");
        } catch (RuntimeException e){
            return new BaseResponse<>("오늘의 미션 update 중 오류 발생");
        }
    }

    /**
     * '미션 완료하기' 버튼 요청 처리
     * 하루에 미션 스탬프 최대 2개까지 적립가능
     */


    /**
     * 미션 맵 현황 조회
     */
    @GetMapping("/{userId}/mission-map")
    public BaseResponse<GetMissionMapResponse> getMissionMapForUser(@PathVariable long userId){
        log.info("[MissionController.getMissionMapForUser]");

        return new BaseResponse<GetMissionMapResponse>(missionService.getMissionMapForUser(userId));
    }



}

package com.example.mocu.Controller;

import com.example.mocu.Common.response.BaseResponse;
import com.example.mocu.Dto.mission.*;
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
     * -> OK
     **/
    @GetMapping("/today-mission/userId={userId}")
    public BaseResponse<List<GetTodayMissionResponse>> getTodayMissionsForUser(
            @PathVariable long userId){
        log.info("[MissionController.getTodayMissionsForUsers]");

        return new BaseResponse<>(missionService.getTodayMissionsForUser(userId));
    }

    /**
     * 오늘의 미션 update
     * 'MOCU앱 출석하기' 는 고정 미션
     * 추가로 2개가 랜덤으로 뽑혀짐
     * -> OK
     * 아래의 URL은 테스트용 URL임
     */
    @PostMapping("/update/today-mission")
    public BaseResponse<String> updateTodayMissions(){
        log.info("[MissionController.updateTodayMissions]");
        try {
            missionService.updateTodayMissions();
            return new BaseResponse<>("오늘의 미션 update 완료");
        } catch (RuntimeException e){
            return new BaseResponse<>("오늘의 미션 update 중 오류 발생");
        }
    }

    /**
     * '미션 완료하기' 버튼 요청 처리
     * 하루에 미션 스탬프 최대 2개까지 적립가능
     * 미션 스탬프 적립 가능한 경우 : 미션 스탬프 +1, 수행한 오늘의 미션 content 값을 return
     * 미션 스탬프 적립 가능하지 않은 경우 : "이미 2개의 미션 스탬프를 획득하였습니다." return
     */
    @PatchMapping("/today-mission/done")
    public BaseResponse<PatchTodayMissionDoneResponse> todayMissionDone(@RequestBody PatchTodayMissionDoneRequest patchTodayMissionDoneRequest){
        log.info("[MissionController.todayMissionDone]");

        return new BaseResponse<>(missionService.todayMissionDone(patchTodayMissionDoneRequest));
    }

    /**
     * 미션 맵 현황 조회
     */
    @GetMapping("/{userId}/mission-map")
    public BaseResponse<GetMissionMapResponse> getMissionMapForUser(@PathVariable(name = "userId") long userId){
        log.info("[MissionController.getMissionMapForUser]");

        return new BaseResponse<>(missionService.getMissionMapForUser(userId));
    }

    /**
     * 미션 맵 완료(최종 보상 수령)
     */
    @PatchMapping("/mission-map/complete")
    public BaseResponse<PatchMissionMapCompleteResponse> completeMissionMap(@RequestBody PatchMissionMapCompleteRequest patchMissionMapCompleteRequest){
        log.info("[MissionController.completeMissionMap]");

        return new BaseResponse<>(missionService.completeMissionMap(patchMissionMapCompleteRequest));
    }

    /**
     * 미션 맵 update
     * 한달에 한번 미션 맵 update
     */
    public BaseResponse<String> updateMissionMap(){
        log.info("[MissionController.updateMissionMap]");

        try {
            missionService.updateMissionMap();
            return new BaseResponse<>("미션 맵 update 완료");
        } catch (RuntimeException e){
            return new BaseResponse<>("미션 맵 update 중 오류 발생");
        }
    }


}

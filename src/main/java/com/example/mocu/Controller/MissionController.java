package com.example.mocu.Controller;

import com.example.mocu.Common.response.BaseResponse;
import com.example.mocu.Dto.mission.GetTodayMissionResponse;
import com.example.mocu.Service.MissionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/mission")
public class MissionController {
    private final MissionService missionService;

    /**
     * 오늘의 미션 목록 조회
     */
    @GetMapping("/{userId}/today-mission")
    public BaseResponse<List<GetTodayMissionResponse>> getTodayMissionsForUser(
            @PathVariable long userId){
        log.info("[MissionController.getTodayMissionsForUsers]");

        return new BaseResponse<>(missionService.getTodayMissionsForUser(userId));
    }



}

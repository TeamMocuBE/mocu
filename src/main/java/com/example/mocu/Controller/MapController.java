package com.example.mocu.Controller;

import com.example.mocu.Common.response.BaseResponse;
import com.example.mocu.Dto.map.GetMapStoreInfoResponse;
import com.example.mocu.Service.MapService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/map")
public class MapController {
    private final MapService mapService;

    /**
     * 지도페이지에서 현 기기 위치 근방 가게들의 정보 조회
     */
    @GetMapping("/{userId}?latitude={latitude}&longitude={longitude}")
    public BaseResponse<List<GetMapStoreInfoResponse>> getMapStoreInfoList(@PathVariable("userId") long userId, @RequestParam("latitude") double latitude, @RequestParam("longitude") double longitude){
        log.info("[MapController.getStoreMapInfoList]");

        return new BaseResponse<>(mapService.getMapStoreInfoList(userId, latitude, longitude));
    }

}


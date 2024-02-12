package com.example.mocu.Controller;

import com.example.mocu.Common.response.BaseResponse;
import com.example.mocu.Dto.map.GetMapStoreInfoResponse;
import com.example.mocu.Dto.map.GetMapStoreResponse;
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
    @GetMapping("/userId={userId}")
    public BaseResponse<List<GetMapStoreResponse>> getMapStoreList(
            @PathVariable(name = "userId") long userId,
            @RequestParam(name = "latitude") double latitude,
            @RequestParam(name = "longitude") double longitude,
            @RequestParam(name = "categoryOption", required = false, defaultValue = "업종 전체") String categoryOption,
            @RequestParam(name = "eventOption", required = false, defaultValue = "false") boolean eventOption,
            @RequestParam(name = "dueDateOption", required = false, defaultValue = "false") boolean dueDateOption){

        log.info("[MapController.getStoreMapList]");

        return new BaseResponse<>(mapService.getMapStoreList(userId, latitude, longitude, categoryOption, eventOption, dueDateOption));
    }

    /**
     * 지도페이지에서 가게 정보 조회
     * -> 404 NOT FOUND ??
     */
    @GetMapping("/store-Info?userId={userId}&storeId={storeId}")
    public BaseResponse<GetMapStoreInfoResponse> getMapStoreInfo(
            @RequestParam(name = "userId") long userId,
            @RequestParam(name = "storeId") long storeId){
        log.info("[MapController.getMapStoreInfo]");

        return new BaseResponse<>(mapService.getMapStoreInfo(userId, storeId));
    }
}


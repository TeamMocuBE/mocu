package com.example.mocu.Controller;

import com.example.mocu.Common.response.BaseResponse;
import com.example.mocu.Dto.stamp.*;
import com.example.mocu.Service.StampService;
import com.fasterxml.jackson.databind.ser.Serializers;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/stamp")
public class StampController {
    private final StampService stampService;

    /**
     * 스탬프 적립 요청(유저 앱 -> 점주 앱)
     */
    @PostMapping("/request")
    public BaseResponse<PostStampResponse> stampRequestRegister(@Validated @RequestBody PostStampRequest postStampRequest){
        log.info("[StampController.stampRequestRegister]");

        return new BaseResponse<>(stampService.stampRequestRegister(postStampRequest));
    }

    /**
     * 스탬프 적립 요청 수락
     * (거절할 경우 그냥 팝업 창 닫기고 끝. db에 update할 정보 없음 -> ??)
     */
    @PostMapping("/owner-accept")
    public BaseResponse<PostStampAcceptResponse> stampRequestAccept(@Validated @RequestBody PostStampAcceptRequest postStampAcceptRequest){
        log.info("[StampController.stampRequestAccept]");

        return new BaseResponse<>(stampService.stampRequestAccept(postStampAcceptRequest));
    }

    /**
     * 홈화면의 '적립' 페이지 조회
     * 유저의 현재 설정 위치 근방의 쿠폰 적립/사용가능한 가게들 return
     * 무한스크롤 구현
     */
    @GetMapping("/stores-around/userId={userId}?latitude={latitude}&longitude={longitude}")
    public BaseResponse<List<GetStampStoreAroundResponse>> getStampStoreAroundList(
            @PathVariable long userId,
            @RequestParam double latitude,
            @RequestParam double longitude,
            @RequestParam(defaultValue = "0") int page){
        log.info("[StampController.getStampStoreAroundList]");

        return new BaseResponse<>(stampService.getStampStoreAroundList(userId, latitude, longitude, page));
    }
}

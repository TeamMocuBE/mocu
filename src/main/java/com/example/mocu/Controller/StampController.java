package com.example.mocu.Controller;

import com.example.mocu.Common.response.BaseResponse;
import com.example.mocu.Dto.stamp.PostStampAcceptRequest;
import com.example.mocu.Dto.stamp.PostStampAcceptResponse;
import com.example.mocu.Dto.stamp.PostStampRequest;
import com.example.mocu.Dto.stamp.PostStampResponse;
import com.example.mocu.Service.StampService;
import com.fasterxml.jackson.databind.ser.Serializers;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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


}

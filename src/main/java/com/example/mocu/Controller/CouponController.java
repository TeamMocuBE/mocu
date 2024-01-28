package com.example.mocu.Controller;

import com.example.mocu.Common.response.BaseResponse;
import com.example.mocu.Dto.coupon.PostCouponAcceptRequest;
import com.example.mocu.Dto.coupon.PostCouponAcceptResponse;
import com.example.mocu.Dto.coupon.PostCouponRequest;
import com.example.mocu.Dto.coupon.PostCouponResponse;
import com.example.mocu.Dto.stamp.PostStampRequest;
import com.example.mocu.Service.CouponService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/coupon")
public class CouponController {
    private final CouponService couponService;

    /**
     * 쿠폰 사용 요청(유저 앱 -> 점주 앱)
     */
    @PostMapping("/request")
    public BaseResponse<PostCouponResponse> couponRequestRegister(@Validated @RequestBody PostCouponRequest postCouponRequest){
        log.info("[CouponController.couponRequestRegister]");

        return new BaseResponse<>(couponService.couponRequestRegister(postCouponRequest));
    }

    /**
     * 쿠폰 사용 요청 수락
     * (거절할 경우 그냥 팝업 창 닫기고 끝. db에 update할 정보 없음 -> ??)
     */
    @PostMapping("/owner-accept")
    public BaseResponse<PostCouponAcceptResponse> couponRequestAccept(@Validated @RequestBody PostCouponAcceptRequest postCouponAcceptRequest){
        log.info("[CouponController.couponRequestAccept]");

        return new BaseResponse<>(couponService.couponRequestAccept(postCouponAcceptRequest));
    }




}

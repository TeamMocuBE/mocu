package com.example.mocu.Controller;

import com.example.mocu.Common.response.BaseResponse;
import com.example.mocu.Dao.OwnerDao;
import com.example.mocu.Dao.UserDao;
import com.example.mocu.Dto.Push.PushRequestToOwner;
import com.example.mocu.Dto.Push.PushRequestToUser;
import com.example.mocu.Dto.coupon.*;
import com.example.mocu.Service.CouponService;
import com.example.mocu.Service.PushService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/coupon")
public class CouponController {
    private final CouponService couponService;
    private final OwnerDao ownerDao;
    private final UserDao userDao;
    private final PushService pushService;

    /**
     * 쿠폰 사용 요청(유저 앱 -> 점주 앱)
     */
    @PostMapping("/request")
    public BaseResponse<String> couponRequestRegister(@RequestBody PostCouponRequest postCouponRequest) {
        log.info("[CouponController.couponRequestRegister]");

        try {
            // TODO 1. PostCouponResponse return
            PostCouponResponse postCouponResponse = couponService.couponRequestRegister(postCouponRequest);
            // -> OK

            // TODO 2. postCouponResponse의 storeId값을 가지고 있는 owner 정보 get
            long ownerId = ownerDao.getOwnerId(postCouponResponse.getStoreId());
            String ownerUuid = ownerDao.getOwnerUuid(ownerId);

            // TODO 3. 푸시 알림 전송 요청 보내기
            PushRequestToOwner pushRequestToOwner = new PushRequestToOwner(
                    ownerUuid,
                    postCouponResponse.getCouponRequestId(),
                    postCouponResponse.getUserId(),
                    postCouponResponse.getStoreId(),
                    postCouponResponse.getCreatedDate(),
                    postCouponResponse.getStoreAddress(),
                    postCouponResponse.getUserName()
            );

            pushService.sendPushMessageToOwner(pushRequestToOwner);


            return new BaseResponse<>("쿠폰 사용 요청 성공");
        } catch (RuntimeException e){
            return new BaseResponse<>("쿠폰 사용 요청 중 오류 발생");
        }
    }

    /**
     * 쿠폰 사용 요청 수락
     * (거절할 경우 그냥 팝업 창 닫기고 끝. db에 update할 정보 없음 -> ??)
     * -> numOfCouponAvailable이 1이상인지 체크하는 로직 추가
     * -> OK
     */
    @PostMapping("/owner-accept")
    public BaseResponse<String> couponRequestAccept(@RequestBody PostCouponAcceptRequest postCouponAcceptRequest) {
        log.info("[CouponController.couponRequestAccept]");

        try {
            // TODO 1. PostCouponAcceptResponse return
            PostCouponAcceptResponse postCouponAcceptResponse = couponService.couponRequestAccept(postCouponAcceptRequest);

            // TODO 2. userId get
            long userId = userDao.getUserId(postCouponAcceptResponse.getStampId());
            String userUuid = userDao.getUserUuid(userId);

            // TODO 3. 푸시 알림 전송 요청 보내기 (to user)
            PushRequestToUser pushRequestToUser = new PushRequestToUser(
                    userUuid,
                    postCouponAcceptResponse.getStampId(),
                    postCouponAcceptResponse.getNumOfStamp(),
                    postCouponAcceptResponse.getMaxStamp(),
                    postCouponAcceptResponse.getStoreName(),
                    postCouponAcceptResponse.isDueDate(),
                    postCouponAcceptResponse.getNumOfCouponAvailable()
            );

            pushService.sendPushMessageToUser(pushRequestToUser);

            return new BaseResponse<>("쿠폰 적립 요청 수락 성공 처리 완료.");
        } catch (RuntimeException e){
            return new BaseResponse<>("쿠폰 적립 요청 수락 성공 처리 중 오류 발생");
        }

    }


    /**
     * 쿠폰 적립 현황
     */
    @GetMapping("/my-coupon/{userId}")
    public BaseResponse<List<GetMyCouponList>> myCouponList(@PathVariable(name = "userId") long userId,
                                                            @RequestParam(name = "category", required = false) String category,
                                                            @RequestParam(name = "sort", required = false, defaultValue = "최신순") String sort,
                                                            @RequestParam(name = "isEventTrue", defaultValue = "false") boolean isEventTrue,
                                                            @RequestParam(name = "isCouponUsable", defaultValue = "false") boolean isCouponUsable,
                                                            @RequestParam(name = "isStoreRegular", defaultValue = "false") boolean isStoreRegular,
                                                            @RequestParam(name = "isCouponCloseToCompletion", defaultValue = "false") boolean isCouponCloseToCompletion) {
        log.info("[CouponController.myCouponList]");

        return new BaseResponse<>(couponService.myCouponList(userId, category, sort, isEventTrue, isCouponUsable, isStoreRegular, isCouponCloseToCompletion));
    }
}

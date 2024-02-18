package com.example.mocu.Controller;

import com.example.mocu.Common.response.BaseResponse;
import com.example.mocu.Dao.OwnerDao;
import com.example.mocu.Dao.UserDao;
import com.example.mocu.Dto.Push.PushRequestToOwner;
import com.example.mocu.Dto.Push.PushRequestToUser;
import com.example.mocu.Dto.stamp.*;
import com.example.mocu.Service.PushService;
import com.example.mocu.Service.StampService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/stamp")
public class StampController {
    private final StampService stampService;
    private final OwnerDao ownerDao;
    private final UserDao userDao;
    private final PushService pushService;

    /**
     * 스탬프 적립 요청
     */
    @PostMapping("/request")
    public BaseResponse<String> stampRequestRegister(@RequestBody PostStampRequest postStampRequest){
        log.info("[StampController.stampRequestRegister]");

        try {
            // TODO 1. PostStampResponse return
            PostStampResponse postStampResponse = stampService.stampRequestRegister(postStampRequest);
            // -> OK

            // TODO 2. push 알림을 보낼 ownerId get
            long ownerId = ownerDao.getOwnerId(postStampResponse.getStoreId());
            String ownerUuid = ownerDao.getOwnerUuid(ownerId);

            // TODO 3. 푸시 알람 전송 요청 보내기 (to owner)
            PushRequestToOwner pushRequestToOwner = new PushRequestToOwner(
                    ownerUuid,
                    postStampResponse.getStampRequestId(),
                    postStampResponse.getUserId(),
                    postStampResponse.getStoreId(),
                    postStampResponse.getCreatedDate(),
                    postStampResponse.getStoreAddress(),
                    postStampResponse.getUserName()
            );

            pushService.sendPushMessageToOwner(pushRequestToOwner);

            return new BaseResponse<>("스탬프 적립 요청 성공");
        } catch (RuntimeException e){
            return new BaseResponse<>("스탬프 적립 요청 중 오류 발생");
        }
    }

    /**
     * 스탬프 적립 요청 수락
     * (거절할 경우 그냥 팝업 창 닫기고 끝. db에 update할 정보 없음 -> ??)
     * -> OK
     * 테스트 더 많이 해볼 것
     */
    @PostMapping("/owner-accept")
    public BaseResponse<String> stampRequestAccept(@RequestBody PostStampAcceptRequest postStampAcceptRequest){
        log.info("[StampController.stampRequestAccept]");

        try {
            // TODO 1. PostStampAcceptResponse return
            PostStampAcceptResponse postStampAcceptResponse = stampService.stampRequestAccept(postStampAcceptRequest);

            // TODO 2. push 알림을 보낼 userId get
            long userId = userDao.getUserId(postStampAcceptResponse.getStampId());
            String userUuid = userDao.getUserUuid(userId);

            // TODO 3. 푸시 알림 전송 요청 보내기 (to user)
            PushRequestToUser pushRequestToUser = new PushRequestToUser(
                    userUuid,
                    postStampAcceptResponse.getStampId(),
                    postStampAcceptResponse.getNumOfStamp(),
                    postStampAcceptResponse.getMaxStamp(),
                    postStampAcceptResponse.getStoreName(),
                    postStampAcceptResponse.isDueDate(),
                    postStampAcceptResponse.getNumOfCouponAvailable()
            );

            pushService.sendPushMessageToUser(pushRequestToUser);

            return new BaseResponse<>("스탬프 적립 요청 수락 성공 처리 완료");
        } catch (RuntimeException e){
            return new BaseResponse<>("스탬프 적립 요청 수락 성공 처리 중 오류 발생");
        }


    }

    /**
     * 홈화면의 '적립' 페이지 조회
     * 유저의 현재 설정 위치 근방의 쿠폰 적립/사용가능한 가게들 return
     * 무한스크롤 구현
     * -> OK
     */
    @GetMapping("/stores-around/userId={userId}")
    public BaseResponse<List<GetStampStoreAroundResponse>> getStampStoreAroundList(
            @PathVariable(name = "userId") long userId,
            @RequestParam(name = "latitude") double latitude,
            @RequestParam(name = "longitude") double longitude,
            @RequestParam(name = "page", defaultValue = "0") int page){
        log.info("[StampController.getStampStoreAroundList]");

        return new BaseResponse<>(stampService.getStampStoreAroundList(userId, latitude, longitude, page));
    }
}

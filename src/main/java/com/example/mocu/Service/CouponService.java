package com.example.mocu.Service;

import com.example.mocu.Dao.CouponDao;
import com.example.mocu.Dao.MissionDao;
import com.example.mocu.Dao.StampDao;
import com.example.mocu.Dao.UserDao;
import com.example.mocu.Dto.coupon.*;
import com.example.mocu.Dto.stamp.StampInfoAfterCouponUse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@Service
@RequiredArgsConstructor
public class CouponService {
    private final CouponDao couponDao;
    private final StampDao stampDao;
    private final MissionDao missionDao;
    private final UserDao userDao;

    public PostCouponResponse couponRequestRegister(PostCouponRequest postCouponRequest) {
        log.info("[CouponService.couponRequestRegister]");

        return couponDao.couponRequestRegister(postCouponRequest);
    }


    public PostCouponAcceptResponse couponRequestAccept(PostCouponAcceptRequest postCouponAcceptRequest) {
        log.info("[CouponService.couponRequestAccept]");

        // TODO 1. CouponsRequest table의 tuple에서 status 값을 'accept'로 변경
        couponDao.updateCouponsRequestStatusToAccept(postCouponAcceptRequest.getCouponRequestId());

        // TODO 2. 해당 가게의 maxStamp 값 찾기
        int maxStamp = stampDao.getMaxStampValue(postCouponAcceptRequest.getStoreId());

        // TODO 3. Stamps table의 tuple에서 numOfStamp, numOfCouponAvailable, useCount 값 update
        // numOfStamp -= maxStamp
        // numOfCouponAvailable -= 1
        // useCount += 1
        StampInfoAfterCouponUse stampInfoAfterCouponUse = couponDao.updateStampsTable(postCouponAcceptRequest, maxStamp);

        // TODO 4. 쿠폰 사용에 대한 보상 찾기
        String reward = couponDao.getStoreReward(postCouponAcceptRequest.getStoreId());

        // TODO 5. 쿠폰 사용 후 쿠폰 사용 임박 여부 체크
        boolean isCouponImminent = checkCouponImminent(stampInfoAfterCouponUse, postCouponAcceptRequest.getStoreId());

        // TODO 6. '단골 등록' 팝업창 띄울지 말지 체크
        boolean regularPopUp;
        // 1. regularId 존재하는지 체크
        if(userDao.isExistRegularId(postCouponAcceptRequest.getUserId(), postCouponAcceptRequest.getStoreId())){
            // 존재하면 status 체크
            String status = userDao.getRegularStatus(postCouponAcceptRequest.getUserId(), postCouponAcceptRequest.getStoreId());
            switch (status){
                case "request" :
                    regularPopUp = true;
                    break;
                case "not-accept" :
                case "accept" :
                    regularPopUp = false;
                    break;
                // Handle unexpected status value (optional)
                default:
                    regularPopUp = false;
                    break;
            }
        }
        else{
            // 존재하지 않으면 regularId 생성
            userDao.createRegularId(postCouponAcceptRequest.getUserId(), postCouponAcceptRequest.getStoreId());
            regularPopUp = true;
        }

        // TODO 7. '쿠폰 사용하기' 가 오늘의 미션에 해당하는지 체크
        // 오늘의 미션 중 '쿠폰 사용하기' 가 있는지 체크
        boolean isTodayMission = false;
        if(missionDao.isTodayMissionAssigned(postCouponAcceptRequest.getUserId(), "쿠폰 사용하기")){
            isTodayMission = true;
        }

        // TODO 8. TODO 6 통과할 경우 '미션 완료' 처리
        if(isTodayMission){
            missionDao.updateTodayMissionToDone(postCouponAcceptRequest.getUserId());
        }

        // TODO 9. RETURN 형식 맞추기
        return buildPostCouponAcceptResponse(postCouponAcceptRequest, stampInfoAfterCouponUse, maxStamp, isCouponImminent, reward, isTodayMission, regularPopUp);
    }

    private PostCouponAcceptResponse buildPostCouponAcceptResponse(PostCouponAcceptRequest postCouponAcceptRequest, StampInfoAfterCouponUse stampInfoAfterCouponUse, int maxStamp, boolean isCouponImminent, String reward, boolean isTodayMission, boolean regularPopUp) {
        String storeName = stampDao.getStoreName(postCouponAcceptRequest.getStoreId());

        return new PostCouponAcceptResponse(
                stampInfoAfterCouponUse.getStampId(),
                stampInfoAfterCouponUse.getNumOfStamp(),
                maxStamp,
                storeName,
                reward,
                isCouponImminent,
                stampInfoAfterCouponUse.getNumOfCouponAvailable(),
                isTodayMission,
                regularPopUp
        );
    }

    private boolean checkCouponImminent(StampInfoAfterCouponUse stampInfoAfterCouponUse, long storeId) {
        // maxStamp value 조회
        int maxStampValue = stampDao.getMaxStampValue(storeId);
        // 적립 임박 요건을 충족하는지 체크
        boolean isDueDateTrue = stampInfoAfterCouponUse.getNumOfStamp() >= 0.8 * maxStampValue;
        // 적립 임박 상태 update
        stampDao.updateDueDate(stampInfoAfterCouponUse.getStampId(), isDueDateTrue);

        return isDueDateTrue;
    }


    public List<GetMyCouponList> myCouponList(long userId, String category, String sort) {
        log.info("[CouponService.myCouponList]");

        return couponDao.myCouponList(userId, category, sort);
    }
}

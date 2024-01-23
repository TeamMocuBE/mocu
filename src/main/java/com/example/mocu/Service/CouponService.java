package com.example.mocu.Service;

import com.example.mocu.Dao.CouponDao;
import com.example.mocu.Dao.StampDao;
import com.example.mocu.Dto.coupon.PostCouponAcceptRequest;
import com.example.mocu.Dto.coupon.PostCouponAcceptResponse;
import com.example.mocu.Dto.coupon.PostCouponRequest;
import com.example.mocu.Dto.coupon.PostCouponResponse;
import com.example.mocu.Dto.stamp.StampInfo;
import com.example.mocu.Dto.stamp.StampInfoAfterCouponUse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@Service
@RequiredArgsConstructor
public class CouponService {
    private final CouponDao couponDao;
    private final StampDao stampDao;

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

        // TODO 6. RETURN 형식 맞추기
        return buildPostCouponAcceptResponse(postCouponAcceptRequest, stampInfoAfterCouponUse, maxStamp, isCouponImminent, reward);
    }

    private PostCouponAcceptResponse buildPostCouponAcceptResponse(PostCouponAcceptRequest postCouponAcceptRequest, StampInfoAfterCouponUse stampInfoAfterCouponUse, int maxStamp, boolean isCouponImminent, String reward) {
        String storeName = stampDao.getStoreName(postCouponAcceptRequest.getStoreId());

        return new PostCouponAcceptResponse(
                stampInfoAfterCouponUse.getStampId(),
                stampInfoAfterCouponUse.getNumOfStamp(),
                maxStamp,
                storeName,
                reward,
                isCouponImminent,
                stampInfoAfterCouponUse.getNumOfCouponAvailable()
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




}

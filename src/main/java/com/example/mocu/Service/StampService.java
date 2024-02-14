package com.example.mocu.Service;

import com.example.mocu.Common.response.BaseResponse;
import com.example.mocu.Dao.MissionDao;
import com.example.mocu.Dao.StampDao;
import com.example.mocu.Dao.StoreDao;
import com.example.mocu.Dao.UserDao;
import com.example.mocu.Dto.mission.IsTodayMission;
import com.example.mocu.Dto.stamp.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class StampService {
    private final StampDao stampDao;
    private final MissionDao missionDao;
    private final StoreDao storeDao;
    private final UserDao userDao;

    public PostStampResponse stampRequestRegister(PostStampRequest postStampRequest) {
        log.info("[StampService.stampRequestRegister]");

        return stampDao.stampRequestRegister(postStampRequest);
    }

    public PostStampAcceptResponse stampRequestAccept(PostStampAcceptRequest postStampAcceptRequest) {
        log.info("[StampService.stampRequestAccept]");

        // TODO 1. StampsRequest table의 tuple에서 status 값을 'accept'로 변경
        stampDao.updateStampsRequestStatusToAccept(postStampAcceptRequest.getStampRequestId());

        // TODO 2. Stamps table에 해당 유저의 적립이 진행된 적이 있는지 체크
        boolean stampsEntryExists = stampDao.doesStampsEntityExist(postStampAcceptRequest.getUserId(), postStampAcceptRequest.getStoreId());

        // TODO 3. 적립이 진행된 적이 있으면 UPDATE, 없으면 CREATE
        StampInfo stampInfo;
        if(stampsEntryExists){
            stampInfo = stampDao.updateNumOfStamp(
                    postStampAcceptRequest.getUserId(),
                    postStampAcceptRequest.getStoreId(),
                    postStampAcceptRequest.getNumOfStamp());
        }
        else{
            stampInfo = stampDao.createNewStampsEntry(
                    postStampAcceptRequest.getUserId(),
                    postStampAcceptRequest.getStoreId(),
                    postStampAcceptRequest.getNumOfStamp());
        }

        // TODO 4. 적립 후 쿠폰 사용 임박 여부 체크 & update
        boolean isCouponImminent = checkCouponImminent(stampInfo, postStampAcceptRequest.getStoreId());

        // TODO 5. 적립 후 쿠폰 활성화 여부 체크 & update
        int updatedNumOfCouponAvailable = updateCouponAvailability(stampInfo, postStampAcceptRequest.getStoreId());

        // TODO 6. '이벤트 중인 가게에서 적립하기' 또는 '단골가게에서 적립하기' 또는 '쿠폰 사용 임박 가게에서 적립하기' 가 오늘의 미션에 해당하는지 체크
        long todayMissionId;
        List<IsTodayMission> todayMissionList = new ArrayList<>();

        if(missionDao.isTodayMissionAssigned(postStampAcceptRequest.getUserId(), "이벤트 중인 가게에서 적립하기") &&
        storeDao.isStoreInEvent(postStampAcceptRequest.getStoreId())){
            IsTodayMission todayMission = new IsTodayMission("이벤트 중인 가게에서 적립하기", true);
            todayMissionList.add(todayMission);

            // 1. get '이벤트 중인 가게에서 적립하기' 의 todayMissionId
            todayMissionId = missionDao.getTodayMissionId(postStampAcceptRequest.getUserId(), "이벤트 중인 가게에서 적립하기");
            // 2. 해당 todayMissionId 를 '미션 완료' 처리
            missionDao.updateTodayMissionToDone(todayMissionId);
        }

        if(missionDao.isTodayMissionAssigned(postStampAcceptRequest.getUserId(), "단골가게에서 적립하기") &&
        userDao.isRegular(postStampAcceptRequest.getUserId(), postStampAcceptRequest.getStoreId())){
            IsTodayMission todayMission = new IsTodayMission("단골가게에서 적립하기", true);
            todayMissionList.add(todayMission);

            // 1. get '단골가게에서 적립하기' 의 todayMissionId
            todayMissionId = missionDao.getTodayMissionId(postStampAcceptRequest.getUserId(), "단골가게에서 적립하기");
            // 2. 해당 todayMissionId 를 '미션 완료' 처리
            missionDao.updateTodayMissionToDone(todayMissionId);
        }

        if(missionDao.isTodayMissionAssigned(postStampAcceptRequest.getUserId(), "쿠폰 사용 임박 가게에서 적립하기") &&
        storeDao.isDueDateStore(postStampAcceptRequest.getUserId(), postStampAcceptRequest.getStoreId())){
            IsTodayMission todayMission = new IsTodayMission("쿠폰 사용 임박 가게에서 적립하기", true);
            todayMissionList.add(todayMission);

            // 1. get '쿠폰 사용 임박 가게에서 적립하기' 의 todayMissionId
            todayMissionId = missionDao.getTodayMissionId(postStampAcceptRequest.getUserId(), "쿠폰 사용 임박 가게에서 적립하기");
            // 2. 해당 todayMissionId 를 '미션 완료' 처리
            missionDao.updateTodayMissionToDone(todayMissionId);
        }

        // TODO 7. RETURN 형식 맞추기
        return buildPostStampAcceptResponse(postStampAcceptRequest, stampInfo, isCouponImminent, updatedNumOfCouponAvailable, todayMissionList);
    }

    private PostStampAcceptResponse buildPostStampAcceptResponse(PostStampAcceptRequest postStampAcceptRequest, StampInfo stampInfo, boolean isCouponImminent, int updatedNumOfCouponAvailable, List<IsTodayMission> todayMissionList) {
        String storeName = stampDao.getStoreName(postStampAcceptRequest.getStoreId());
        int maxStamp = stampDao.getMaxStampValue(postStampAcceptRequest.getStoreId());

        return new PostStampAcceptResponse(
                stampInfo.getStampId(),
                stampInfo.getNumOfStamp(),
                maxStamp,
                storeName,
                isCouponImminent,
                updatedNumOfCouponAvailable,
                todayMissionList
        );
    }

    private int updateCouponAvailability(StampInfo stampInfo, long storeId) {
        log.info("[StampService.updateCouponAvailability]");

        // maxStamp value 조회
        int maxStampValue = stampDao.getMaxStampValue(storeId);
        // 사용가능한 쿠폰의 개수 계산
        int numOfCouponAvailable = stampInfo.getNumOfStamp() / maxStampValue;
        // numOfCouponAvailable update
        int updatedNumOfCouponAvailable = stampDao.updateNumOfCouponAvailable(stampInfo.getStampId(), numOfCouponAvailable);

        return updatedNumOfCouponAvailable;
    }

    private boolean checkCouponImminent(StampInfo stampInfo, long storeId) {
        log.info("[StampService.checkCouponImminent]");

        // maxStamp value 조회
        int maxStampValue = stampDao.getMaxStampValue(storeId);
        // 적립 임박 요건을 충족하는지 체크
        boolean isDueDateTrue = stampInfo.getNumOfStamp() >= 0.8 * maxStampValue;
        // 적립 임박 상태 update
        stampDao.updateDueDate(stampInfo.getStampId(), isDueDateTrue);

        return isDueDateTrue;
    }


    public List<GetStampStoreAroundResponse> getStampStoreAroundList(long userId, double latitude, double longitude, int page) {
        log.info("[StampService.getStampStoreAroundList]");

        return stampDao.getStampStoreAroundList(userId, latitude, longitude, page);
    }
}

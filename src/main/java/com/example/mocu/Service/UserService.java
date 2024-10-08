package com.example.mocu.Service;

import com.example.mocu.Dao.MissionDao;
import com.example.mocu.Dao.UserDao;
import com.example.mocu.Dto.user.*;
import com.example.mocu.Exception.DatabaseException;
import com.example.mocu.jpaRepository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static com.example.mocu.Common.response.status.BaseResponseStatus.DATABASE_ERROR;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {
    private final UserDao userDao;
    private final MissionDao missionDao;
    //---------------------------------//
    private final JpaUserRepository userRepository;
    private final JpaStampRepository stampRepository;
    private final JpaRegularRepository regularRepository;
    private final JpaAddressRepository addressRepository;
    private final JpaCouponRequestRepository couponRequestRepository;
    private final JpaReviewRepository reviewRepository;
    private final JpaMissionStampRepository missionStampRepository;

    public List<GetUserResponse> getUsersV1(String name, String email, String status) {
        log.info("[UserService.getUsers]");
        //return userDao.getUsers(name, email, status);
        return userDao.getUsersV2(name, status);
    }

    public List<GetUserResponse> getUsersV2(String name, String email, String status) {
        log.info("[UserService.getUsersV2]");

/*        List<User> users = jpaUserRepository.findUsers(name, email, status);

        return users.stream()
                .map(GetUserResponse::new)
                .collect(Collectors.toList());*/

        return userRepository.findUsersDto(name, email, status);
    }

    public GetMyPageResponse getMypageV1(Long userId) {
        log.info(("[UserService.getMypage]"));
        return userDao.getMypage(userId);
    }

    public GetMyPageResponse getMypageV2(Long userId) {
        log.info(("[UserService.getMypageV2]"));

        Integer usableCoupon = stampRepository.findUsableCouponsById(userId);
        if (usableCoupon == null) {
            usableCoupon = 0;
        }

        Integer availableFavoriteCount = regularRepository.countAvailableFavoriteStores(userId);
        if (availableFavoriteCount == null) {
            availableFavoriteCount = 0;
        }

        String currentAddress = addressRepository.findCurrentAddress().orElse("");

        LocalDate oneMonthAgo = LocalDate.now().minusMonths(1);
        List<GetMyPageResponse.CouponUsageDetail> recentCouponUsage = couponRequestRepository.findRecentCouponUsage(userId, oneMonthAgo);

        Integer availableReviewCount = reviewRepository.findAvailableReviewCount(userId);
        if (availableReviewCount == null) {
            availableReviewCount = 0;
        }

        Integer missionStampCount = missionStampRepository.findMissionStampCount(userId);
        if (missionStampCount == null) {
            missionStampCount = 0;
        }

        return new GetMyPageResponse(
                usableCoupon,
                availableFavoriteCount,
                currentAddress,
                recentCouponUsage,
                availableReviewCount,
                missionStampCount
        );
    }

    public PatchUserRegularResponse handleRegularRequest(PatchUserRegularRequest patchUserRegularRequest) {
        log.info("[UserService.handleRegularRequest]");

        // TODO 1. regularId 존재하는지 확인
        if(!userDao.isExistRegularId(patchUserRegularRequest.getUserId(), patchUserRegularRequest.getStoreId())){
            userDao.createRegularId(patchUserRegularRequest.getUserId(), patchUserRegularRequest.getStoreId());
        }

        // TODO 2. Regular table에서 tuple의 status update
        userDao.updateRegularStatus(patchUserRegularRequest);

        // TODO 3. '단골 맺기' 가 오늘의 미션에 해당되는지 & '단골 맺기'의 상태가 'accept' 인지 체크
        // 오늘의 미션 중 '단골 맺기' 가 있는지 &  체크
        boolean isTodayMission = false;
        if(missionDao.isTodayMissionAssigned(patchUserRegularRequest.getUserId(), "단골 맺기") &&
        userDao.isRegular(patchUserRegularRequest.getUserId(), patchUserRegularRequest.getStoreId())){
            isTodayMission = true;
            // 1. get '단골 맺기' 의 todayMissionId
            long todayMissionId = missionDao.getTodayMissionId(patchUserRegularRequest.getUserId(), "단골 맺기");
            // 2. 해당 todayMissionId 를 '미션 완료' 처리
            missionDao.updateTodayMissionToDone(todayMissionId);
        }

        // TODO 3. RETURN
        long regularId= userDao.getRegularId(patchUserRegularRequest.getUserId(), patchUserRegularRequest.getStoreId());
        return new PatchUserRegularResponse(regularId, isTodayMission);
    }

    public GetMyStoreListResponse getMyStoreList(long userId, String category, String sort, boolean isEventTrue, boolean isCouponUsable, double userLatitude, double userLongitude, int page) {
        log.info("[UserService.getMyStoreList]");

        List<GetRegularResponse> storeList = userDao.getMyStoreList(userId, category, sort, isEventTrue, isCouponUsable, userLatitude, userLongitude, page);

        int regularsCount = userDao.getRegularsCount(userId);

        return new GetMyStoreListResponse(regularsCount, storeList);
    }


    public List<GetStoreCanBeRegularResponse> getStoreCanBeRegularList(long userId, double userLatitude, double userLongitude, int page) {
        log.info("[UserService.getStoreCanBeRegularList]");

        return userDao.getStoreCanBeRegularList(userId, userLatitude, userLongitude, page);
    }


    public void updateRegularStatusToNotAccept(PatchUserRegularToNotAcceptRequest patchUserRegularToNotAcceptRequest) {
        log.info("[UserService.updateRegularStatusToNotAccept]");

        int affectedRows = userDao.updateRegularStatusToNotAccept(patchUserRegularToNotAcceptRequest);
        if(affectedRows != 1){
            throw new DatabaseException(DATABASE_ERROR);
        }
    }
  
    public int getUserIdCount(Long userId) {
        log.info("[UserService.getUserIdCount]");

        return userDao.getUserIdCount(userId);
    }
}

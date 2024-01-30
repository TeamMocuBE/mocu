package com.example.mocu.Service;

import com.example.mocu.Dto.review.ReviewForUser;
import com.example.mocu.Dto.stamp.UserStampInfo;
import com.example.mocu.Dto.store.*;
import com.example.mocu.Exception.StoreException;
import com.example.mocu.Dao.StoreDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.example.mocu.Common.response.status.BaseResponseStatus.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class StoreService {
    private final StoreDao storeDao;
    public GetDetailedStoreResponse getDetailedStore(long storeId, long userId) {
        log.info("[StoreService.getDetailed]");

        // TODO 1. GET 가게 이미지 URL LIST
        List<String> storeImages = storeDao.getStoreImages(storeId);

        // TODO 2. GET 가게 정보
        // storeName, category, maxStamp, reward, rating
        StoreInfo storeInfo = storeDao.getStoreInfo(storeId);

        // TODO 3. GET USER 스탬프 정보
        // 처음 방문하는 user는 numOfStamp와 numOfCouponAvailable 값을 0으로 세팅
        UserStampInfo userStampInfo = storeDao.getUserStampInfo(storeId, userId);


        // TODO 4. GET ReviewForUser List
        // 가게에 등록된 리뷰가 없을 경우 empty list return
        List<ReviewForUser> reviews = storeDao.getReviews(storeId, userId);

        // TODO 5. GetDetailedStoreResponse 형식 맞추기
        return new GetDetailedStoreResponse(
                storeImages,
                storeInfo.getCategory(),
                storeInfo.getStoreName(),
                userStampInfo.getNumOfStamp(),
                userStampInfo.getNumOfCouponAvailable(),
                storeInfo.getMaxStamp(),
                storeInfo.getReward(),
                storeInfo.getRating(),
                reviews
        );
    }

    public List<GetStoreReviewsResponse> getStoreReviews(long storeId, String orderType) {
        log.info("[StoreService.getStoreReviews]");

        switch (orderType){
            case "time":
                return storeDao.getStoreReviewsOrderByTime(storeId);

            case "rate":
                return storeDao.getStoreReviewsOrderByRate(storeId);

            default:
                throw new StoreException(INVALID_STORE_REVIEW_REQUEST_VALUE);
        }
    }


    public List<GetSearchedStoreResponse> getSearchedStore(long userId, String query, String sort, String category, String option) {
        log.info("[StoreService.getSearchedStore");

        //TODO: 올바른 검색인지 체크 (필요한가?)

        return storeDao.getSearchedStore(userId, query, sort, category, option);
    }
}

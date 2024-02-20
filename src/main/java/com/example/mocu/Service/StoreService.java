package com.example.mocu.Service;

import com.example.mocu.Dao.RecommendDao;
import com.example.mocu.Dao.StampDao;
import com.example.mocu.Dao.UserDao;
import com.example.mocu.Dto.review.ReviewForUser;
import com.example.mocu.Dto.search.Search;
import com.example.mocu.Dto.stamp.UserStampInfo;
import com.example.mocu.Dto.store.*;
import com.example.mocu.Exception.StoreException;
import com.example.mocu.Dao.StoreDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.example.mocu.Common.response.status.BaseResponseStatus.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class StoreService {
    private final StoreDao storeDao;
    private final UserDao userDao;
    private final RecommendDao recommendDao;
    private final StampDao stampDao;

    public GetDetailedStoreResponse getDetailedStore(long storeId, long userId, boolean timeSort, boolean rateSort, int page) {
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
        // sort, 무한스크롤 구현
        List<ReviewForUser> reviews = new ArrayList<>();
        if(timeSort){
            // '최신순' 정렬
            reviews = storeDao.getReviewsOrderByTime(storeId, page);
        }
        else if(rateSort){
            // '평점순' 정렬
            reviews = storeDao.getReviewsOrderByRate(storeId, page);
        }
        else{
            // 예외 처리
            throw new StoreException(INVALID_STORE_REVIEW_REQUEST_VALUE);
        }

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
                reviews             // -> 리펙토링 필요??
        );
    }


    public List<GetSearchedStoreResponse> getSearchedStore(long userId, String query, String sort, String category, boolean savingOnly, boolean notVisitedOnly, boolean couponImminent, boolean eventOnly, double userLatitude, double userLongitude, int page) {
        log.info("[StoreService.getSearchedStore]");

        // 검색 조건에 따라 DAO 메소드 호출
        return storeDao.getSearchedStore(userId, query, sort, category, savingOnly, notVisitedOnly, couponImminent, eventOnly, userLatitude, userLongitude, page);
    }

    public GetStoreSearchResponse getStoreSearch(long userId, double latitude, double longitude) {
        log.info("[StoreService.getStoreSearch]");
        /**
         * 유저의 현재 설정한 위치를 기준으로 근방의 가게들을 return하는 방식으로 수정해야 할 듯
         */

        // TODO 1. 최근 검색어 LIST 조회
        // 없으면 모든 멤벼변수가 null로 초기화된 Search 하나를 가진 list return
        List<Search> recentSearches = userDao.getRecentSearchesForUser(userId);
        if(recentSearches == null){
            // recentSearches = Collections.singletonList(new Search());
            recentSearches = null;
        }

        // TODO 2. 최근 방문한 가게 LIST 조회
        // 해당 user가 스탬프 적립 & 쿠폰 사용한 가게 LIST
        // 없으면 모든 멤벼변수가 null로 초기화된 RecentlyVisitedStoreInfo 하나를 가진 list return
        // 1. 해당 유저가 적립한 적이 있는 storeId값을 거리순으로 get
        List<Long> storeIds = stampDao.getStoreIdsStampedByUser(userId, latitude, longitude);
        List<RecentlyVisitedStoreInfo> recentlyVisitedStoreInfoList = new ArrayList<>();
        if(storeIds != null){
            // 2. storeIds 에 있는 storeId를 가지고 RecentlyVisitedStoreInfo return
            for(long storeId : storeIds){
                RecentlyVisitedStoreInfo recentlyVisitedStoreInfo = userDao.getRecentlyVisitedStoreInfoForUser(storeId, userId, latitude, longitude);
                recentlyVisitedStoreInfoList.add(recentlyVisitedStoreInfo);
            }
        }

        // TODO 3. 이벤트 진행 중인 가게 LIST 조회
        // 현재 이벤트 진행 중인 가게들을 랜덤하게 골라서 RETURN (일단은)
        // 없으면 모든 멤벼변수가 null로 초기화된 StoreInEventInfo 하나를 가진 list return
        List<StoreInEventInfo> storeInEventInfoList = storeDao.getStoreInEventInfoList(latitude, longitude);
        if(storeInEventInfoList == null){
            // storeInEventInfoList = Collections.singletonList(new StoreInEventInfo());
            storeInEventInfoList = null;
        }

        // TODO 4. 쿠폰 사용 임박 가게 LIST 조회
        // 없으면 모든 멤벼변수가 null로 초기화된 DueDateStoreInfo 하나를 가진 list return
        // 1. 해당 유저가 적립한 적이 있는 storeId값을 거리순으로 get
        // -> storeIds 에 담겨 있음
        // 2. storeIds 에 있는 storeId를 가지고 DueDateStoreInfo return
        List<DueDateStoreInfo> dueDateStoreInfoList = new ArrayList<>();
        if(storeIds != null){
            for(long storeId : storeIds){
                DueDateStoreInfo dueDateStoreInfo = userDao.getDueDateStoreInfoForUser(storeId, userId, latitude, longitude);
                dueDateStoreInfoList.add(dueDateStoreInfo);
            }
        }

        /*
        // TODO 5. 유저별 맞춤 가게 LIST 조회
        // 1. user가 적립한 가게들을 카테고리를 기준으로 정렬하여 상위 카테고리 numOfCategory 개 선정
        // 2. 1의 결과에 해당하는 가게들을 가게 평점 높은순으로 각 카테고리별 recommendLimit 개수만큼 return
        // -> 유저가 적립한 가게가 없을 때는? -> 전체 가게들중 평점 높은순으로 recommendLimitForNewUser 개수만큼 return
        int numOfCategory = 2;
        int recommendLimitForNewUser = 4;
        int recommendLimit = 2;
        // numOfCategory * recommendLimit = recommendLimitForNewUser

        List<RecommendStoreInfo> recommendStoreInfoList;
        if(storeIds == null){
            // user가 적립한 가게가 없는 경우
            // user의 현 위치에서 가깝고 평점이 높은 가게 순으로 추천
            recommendStoreInfoList = recommendDao.getRecommendStoreInfoListForNewUser(latitude, longitude, recommendLimitForNewUser);
        }
        else{
            // 1. user가 적립한 가게들을 카테고리를 기준으로 최신 적립순으로 정렬 후, 상위 numOfCategory 개수만큼의 카테고리들 값을 return
            List<String> categories = recommendDao.getFavoriteCategories(storeIds, userId, numOfCategory);

            // 2. 1의 결과에 해당하는 카테고리들의 가게들을 가게 평점 높은순으로 각 카테고리별 recommendLimit 개수만큼 return
            /**
              *-> 해당 user가 적립한 적이 없는 가게들을 추천해주지는 못함 -> 마지막에 도전해볼것

            recommendStoreInfoList = new ArrayList<>();
            for(String category : categories){
                RecommendStoreInfo recommendStoreInfo = recommendDao.getRecommendStoreInfo(category, latitude, longitude, recommendLimit);
                recommendStoreInfoList.add(recommendStoreInfo);
            }
        }
        */

        // TODO 6. RETURN
        return new GetStoreSearchResponse(recentSearches, recentlyVisitedStoreInfoList, storeInEventInfoList, dueDateStoreInfoList);
    }
}

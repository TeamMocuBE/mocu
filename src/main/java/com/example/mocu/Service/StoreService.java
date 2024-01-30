package com.example.mocu.Service;

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
    public GetDetailedStoreResponse getDetailedStore(long storeId) {
        log.info("[StoreService.getDetailed]");

        return storeDao.getDetailedStore(storeId);
    }

    public List<GetStoreImagesResponse> getStoreImages(long storeId) {
        log.info("[StoreService.getStoreImages]");

        return storeDao.getStoreImages(storeId);
    }

    public GetNumberOfStampStoreResponse getStoreStamps(long storeId, long userId) {
        log.info("[StoreService.getStoreStamps]");

        // TODO 1. 스탬프 적립이 처음인지 체크
        if(!storeDao.isNotFirstStamp(storeId, userId)){
            return new GetNumberOfStampStoreResponse(0);
        }

        return storeDao.getStoreStamps(storeId, userId);
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

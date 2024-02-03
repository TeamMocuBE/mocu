package com.example.mocu.Controller;

import com.example.mocu.Common.response.BaseResponse;
import com.example.mocu.Dto.store.*;
import com.example.mocu.Service.StoreService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/store")
@RequiredArgsConstructor
public class StoreController {
    private final StoreService storeService;

    /**
     * 가게 상세정보 페이지
     * 리뷰는 최신순 정렬이 default
     */
    @GetMapping("/detail?storeId={storeId}&userId={userId}")
    public BaseResponse<GetDetailedStoreResponse> getDetailedStore(@RequestParam("storeId") long storeId, @RequestParam("userId") long userId){
        log.info("[StoreController.getDetailedStore]");

        return new BaseResponse<>(storeService.getDetailedStore(storeId, userId));
    }


    /**
     * 가게 리뷰 리스트 조회(최신순, 평점순)
     */
    @GetMapping("/detail/reviews?storeId={storeId}&sort-by={orderType}")
    public BaseResponse<List<GetStoreReviewsResponse>> getStoreReviews(@RequestParam("storeId") long storeId, @RequestParam("sort-by") String orderType) {
        log.info("[StoreController.getStoreReviews]");

        return new BaseResponse<>(storeService.getStoreReviews(storeId, orderType));
    }

    /**
     * 가게 검색
     */
    @GetMapping("/store-search-result/{userId}")
    public BaseResponse<List<GetSearchedStoreResponse>> getSearchedStore(
            @PathVariable("userId") long userId,
            @RequestParam(required = false) String query,
            @RequestParam(required = false, defaultValue = "defaultSort") String sort,
            @RequestParam(required = false) String category,
            @RequestParam(defaultValue = "false") boolean savingOption,
            @RequestParam(defaultValue = "false") boolean notVisitedOption,
            @RequestParam(defaultValue = "false") boolean couponImminentOption,
            @RequestParam(defaultValue = "false") boolean eventOption,
            @RequestParam double userLatitude,
            @RequestParam double userLongitude) {
        log.info("[StoreController.getSearchedStore] Search Query: {}", query);

        return new BaseResponse<>(storeService.getSearchedStore(userId, query, sort, category, savingOption, notVisitedOption, couponImminentOption, eventOption, userLatitude, userLongitude));
    }

    /**
     * 가게 검색 페이지 조회
     */
    @GetMapping("/store-search/userId={userId}")
    public BaseResponse<GetStoreSearchResponse> getStoreSearch(@PathVariable("userId") long userId){
        log.info("[StoreController.getStoreSearch]");

        return new BaseResponse<>(storeService.getStoreSearch(userId));
    }

    /**
     * 가게 평점 update
     */

}

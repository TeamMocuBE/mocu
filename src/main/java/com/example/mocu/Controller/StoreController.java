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
     * 무한스크롤 구현
     */
    @GetMapping("/detail")
    public BaseResponse<GetDetailedStoreResponse> getDetailedStore(
            @RequestParam("storeId") long storeId,
            @RequestParam("userId") long userId,
            @RequestParam(name = "timeSort", required = false, defaultValue = "true") boolean timeSort,
            @RequestParam(name = "rateSort", required = false, defaultValue = "false") boolean rateSort,
            @RequestParam(name = "page", defaultValue = "0") int page){
        log.info("[StoreController.getDetailedStore]");

        return new BaseResponse<>(storeService.getDetailedStore(storeId, userId, timeSort, rateSort, page));
    }


    /**
     * 가게 검색
     */
    @GetMapping("/store-search-result/{userId}")
    public BaseResponse<List<GetSearchedStoreResponse>> getSearchedStore(
            @PathVariable(name = "userId") long userId,
            @RequestParam(name = "query", required = false) String query,
            @RequestParam(name = "sort", required = false, defaultValue = "defaultSort") String sort,
            @RequestParam(name = "category", required = false) String category,
            @RequestParam(name = "savingOption", defaultValue = "false") boolean savingOption,
            @RequestParam(name = "notVisitedOption", defaultValue = "false") boolean notVisitedOption,
            @RequestParam(name = "couponImminentOption", defaultValue = "false") boolean couponImminentOption,
            @RequestParam(name = "eventOption", defaultValue = "false") boolean eventOption,
            @RequestParam(name = "userLatitude") double userLatitude,
            @RequestParam(name = "userLongitude") double userLongitude,
            @RequestParam(name = "page", defaultValue = "0") int page) {
        log.info("[StoreController.getSearchedStore] Search Query: {}", query);

        return new BaseResponse<>(storeService.getSearchedStore(userId, query, sort, category, savingOption, notVisitedOption, couponImminentOption, eventOption, userLatitude, userLongitude, page));
    }

    /**
     * 가게 검색 페이지 조회
     * -> 수정 필요(거리순 추천 기능 추가해야함)
     */
    @GetMapping("/store-search/userId={userId}")
    public BaseResponse<GetStoreSearchResponse> getStoreSearch(@PathVariable("userId") long userId){
        log.info("[StoreController.getStoreSearch]");

        return new BaseResponse<>(storeService.getStoreSearch(userId));
    }


}

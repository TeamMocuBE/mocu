package com.example.mocu.Controller;

import com.example.mocu.Common.response.BaseResponse;
import com.example.mocu.Dto.store.GetDetailedStoreResponse;
import com.example.mocu.Dto.store.GetNumberOfStampStoreResponse;
import com.example.mocu.Dto.store.GetStoreImagesResponse;
import com.example.mocu.Dto.store.GetStoreReviewsResponse;
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
     * 가게 상세정보 조회
     */
    @GetMapping("/detail/{storeId}")
    public BaseResponse<GetDetailedStoreResponse> getDetailedStore(@PathVariable("storeId") long storeId){
        log.info("[StoreController.getDatailed]");

        return new BaseResponse<>(storeService.getDetailedStore(storeId));
    }

    /**
     * 가게 이미지 조회
     */
    @GetMapping("/detail/images/{storeId}")
    public BaseResponse<List<GetStoreImagesResponse>> getStoreImages(@PathVariable("storeId") long storeId){
        log.info("[StoreController.getStoreIamges]");

        return new BaseResponse<>(storeService.getStoreImages(storeId));
    }

    /**
     * 해당가게에 적립한 스탬프 수 조회
     * 적립한 적이 없는 가게이면 numberOfStamp = 0
     */
    @GetMapping("/stamp?storeId={storeId}&userId={userId}")
    public BaseResponse<GetNumberOfStampStoreResponse> getStoreStamps(@RequestParam("storeId") long storeId, @RequestParam("userId") long userId){
        log.info("[StoreController.getStoreStamps]");

        return new BaseResponse<>(storeService.getStoreStamps(storeId, userId));
    }

    /**
     * 가게 리뷰 리스트 조회(최신순, 평점순)
     */
    @GetMapping("/reviews?storeId={storeId}&sort-by={orderType}")
    public BaseResponse<List<GetStoreReviewsResponse>> getStoreReviews(@RequestParam("storeId") long storeId, @RequestParam("sort-by") String orderType){
        log.info("[StoreController.getStoreReviews]");

        return new BaseResponse<>(storeService.getStoreReviews(storeId, orderType));
    }


}

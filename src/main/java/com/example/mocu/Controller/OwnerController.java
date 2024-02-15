package com.example.mocu.Controller;

import com.example.mocu.Common.response.BaseResponse;
import com.example.mocu.Dto.owner.*;
import com.example.mocu.Service.OwnerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/owner")
public class OwnerController {
    private final OwnerService ownerService;

    /**
    * 고객 요청관리 조회
    * 점주 앱 고객 요청 관리 페이지
    * 필터링, 무한스크롤 구현
     * -> OK
    */
    @GetMapping("/store-request/storeId={storeId}")
    public BaseResponse<List<GetUserRequestForOwner>> getUserRequestListForOwner(
            @PathVariable(name = "storeId") long storeId,
            @RequestParam(name = "notAcceptRequest", required = false, defaultValue = "false") boolean notAcceptRequest,
            @RequestParam(name = "bothRequest", required = false, defaultValue = "true") boolean bothRequest,
            @RequestParam(name = "rewardRequest", required = false, defaultValue = "false") boolean rewardRequest,
            @RequestParam(name = "stampRequest", required = false, defaultValue = "false") boolean stampRequest,
            @RequestParam(name = "page", defaultValue = "0") int page){

        log.info("[OwnerController.getUserRequestListForOwner]");

        return new BaseResponse<>(ownerService.getUserRequestListForOwner(storeId, notAcceptRequest, bothRequest, rewardRequest, stampRequest, page));
    }

    /**
     * 가게 정보 조회
     * -> OK
     */
    @GetMapping("/store-info/storeId={storeId}")
    public BaseResponse<GetOwnerStoreInfoResponse> getStoreInfoForOwner(@PathVariable("storeId") long storeId){
        log.info("[OwnerController.getStoreInfoForOwner]");

        return new BaseResponse<>(ownerService.getStoreInfoForOwner(storeId));
    }

    /**
     * 가게 정보 등록
     * -> OK
     */
    @PostMapping("/store-register")
    public BaseResponse<PostOwnerStoreResponse> registerStore(@RequestBody PostOwnerStoreRequest postOwnerStoreRequest){
        log.info("[OwnerController.registerStore]");

        return new BaseResponse<>(ownerService.registerStore(postOwnerStoreRequest));
    }
  
    /**
     * 가게 정보 수정
     * -> OK
     */
    @PatchMapping("/store-edit")
    public BaseResponse<String> modifyStoreInfo(@RequestBody PatchOwnerStoreRequest patchOwnerStoreRequest){
        log.info("[OwnerController.modifyStoreInfo]");

        ownerService.modifyStoreInfo(patchOwnerStoreRequest);
        return new BaseResponse<>("가게 정보 수정 완료");
    }

    /**
     * 고객 적립 현황
     */
    @GetMapping("/{ownerId}/stamp-of-customer")
    public BaseResponse<List<GetCustomerStampResponse>> getCustomerStamp(@PathVariable(name = "ownerId") Long ownerId,
                                                                         @RequestParam(name = "isCustomerRegular", required = false, defaultValue = "false") boolean isCustomerRegular,
                                                                         @RequestParam(name = "sort", defaultValue = "적립 높은 순") String sort) {
        log.info("[OwnerController.getCustomerStamp]");

        return new BaseResponse<>(ownerService.getCustomerStamp(ownerId, isCustomerRegular, sort));
    }

}

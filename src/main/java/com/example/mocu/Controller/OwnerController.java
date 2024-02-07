package com.example.mocu.Controller;

import com.example.mocu.Common.response.BaseResponse;
import com.example.mocu.Dto.owner.*;
import com.example.mocu.Service.OwnerService;
import com.fasterxml.jackson.databind.ser.Serializers;
import lombok.*;
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
     */
    @GetMapping("/store-request/storeId={storeId}")
    public BaseResponse<List<GetUserRequestForOwner>> getUserRequestListForOwner(
            @PathVariable("storeId") long storeId,
            @RequestParam(required = false, defaultValue = "false") boolean notAcceptRequest,
            @RequestParam(required = false, defaultValue = "true") boolean bothRequest,
            @RequestParam(required = false, defaultValue = "false") boolean rewardRequest,
            @RequestParam(required = false, defaultValue = "false") boolean stampRequest,
            @RequestParam(defaultValue = "0") int page){
        // -> page의 defaulValue 1이어야 하지 않나??

        log.info("[OwnerController.getUserRequestListForOwner]");

        return new BaseResponse<>(ownerService.getUserRequestListForOwner(storeId, notAcceptRequest, bothRequest, rewardRequest, stampRequest, page));
    }


    /**
     * 가게 정보 등록
     */
    @PostMapping("/store-register")
    public BaseResponse<PostOwnerStoreResponse> registerStore(@Validated @RequestBody PostOwnerStoreRequest postOwnerStoreRequest){
        log.info("[OwnerController.registerStore]");

        return new BaseResponse<>(ownerService.registerStore(postOwnerStoreRequest));
    }

    /**
     * 가게 정보 수정
     */
    @PatchMapping("/store-edit")
    public BaseResponse<String> modifyStoreInfo(@Validated @RequestBody PatchOwnerStoreRequest patchOwnerStoreRequest){
        log.info("[OwnerController.modifyStoreInfo]");

        ownerService.modifyStoreInfo(patchOwnerStoreRequest);
        return new BaseResponse<>("가게 정보 수정 완료");
    }

    /**
     * 가게 정보 조회
     */
    @GetMapping("/store-info/storeId={storeId}")
    public BaseResponse<GetOwnerStoreInfoResponse> getStoreInfoForOwner(@PathVariable("storeId") long storeId){
        log.info("[OwnerController.getStoreInfoForOwner]");

        return new BaseResponse<>(ownerService.getStoreInfoForOwner(storeId));
    }

}

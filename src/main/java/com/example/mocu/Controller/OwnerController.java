package com.example.mocu.Controller;

import com.example.mocu.Common.response.BaseResponse;
import com.example.mocu.Dto.owner.*;
import com.example.mocu.Service.OwnerService;
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
     * 미수락 스탬프 적립 요청 목록 조회
     */
    @GetMapping("/{storeId}/stamp/not-accept")
    public BaseResponse<List<GetOwnerStampNotAcceptResponse>> getStampsNotAccept(
            @PathVariable long storeId){
        log.info("[OwnerController.getStampNotAccept]");

        return new BaseResponse<>(ownerService.getStampsNotAccept(storeId));
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

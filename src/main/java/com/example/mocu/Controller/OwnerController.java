package com.example.mocu.Controller;

import com.example.mocu.Common.response.BaseResponse;
import com.example.mocu.Dto.owner.PatchOwnerStoreRequest;
import com.example.mocu.Dto.owner.PostOwnerStoreRequest;
import com.example.mocu.Dto.owner.PostOwnerStoreResponse;
import com.example.mocu.Service.OwnerService;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/owner")
public class OwnerController {
    private final OwnerService ownerService;


    /**
     * 스탬프 적립 요청 처리
     * 점주 어플에서 스탬프 적립 요청 수락하는 경우
     */
    /*
    @PutMapping("/stamp")
    public BaseResponse<PutStampResponse> registerStamp(@Validated @RequestBody PutStampRequest putStampRequest){
        log.info("[OwnerController.registerStamp]");

        return new BaseResponse<>(ownerService.registerStamp(putStampRequest));
    }
    */


    /**
     * 가게 정보 등록
     */
    @PostMapping("/store")
    public BaseResponse<PostOwnerStoreResponse> registerStore(@Validated @RequestBody PostOwnerStoreRequest postOwnerStoreRequest){
        log.info("[OwnerController.registerStore]");

        return new BaseResponse<>(ownerService.registerStore(postOwnerStoreRequest));
    }

    /**
     * 가게 정보 수정
     */
    @PatchMapping("/store/{storeId}")
    public BaseResponse<String> modifyStoreInfo(@PathVariable long storeId, @Validated @RequestBody PatchOwnerStoreRequest patchOwnerStoreRequest){
        log.info("[OwnerController.modifyStoreInfo]");

        ownerService.modifyStoreInfo(storeId, patchOwnerStoreRequest);
        return new BaseResponse<>("가게 정보 수정 완료.");
    }


}

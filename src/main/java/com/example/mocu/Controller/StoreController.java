package com.example.mocu.Controller;

import com.example.mocu.Common.response.BaseResponse;
import com.example.mocu.Dto.store.GetDetailedStoreResponse;
import com.example.mocu.Service.StoreService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}

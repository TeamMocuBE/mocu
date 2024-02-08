package com.example.mocu.Service;

import com.example.mocu.Dao.OwnerDao;
import com.example.mocu.Dto.owner.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@Service
@RequiredArgsConstructor
public class OwnerService {
    private final OwnerDao ownerDao;

    public PostOwnerStoreResponse registerStore(PostOwnerStoreRequest postOwnerStoreRequest) {
        log.info("[OwnerService.registerStore]");

        long storeId = ownerDao.registerStore(postOwnerStoreRequest);
        return new PostOwnerStoreResponse(storeId);
    }

    public void modifyStoreInfo(long storeId, PatchOwnerStoreRequest patchOwnerStoreRequest) {
        log.info("[OwnerService.modifyStoreInfo]");

        ownerDao.modifyStoreInfo(storeId, patchOwnerStoreRequest);
    }

    public List<GetOwnerStampNotAcceptResponse> getStampsNotAccept(long storeId) {
        log.info("[OwnerService.getStampsNotAccept]");

        return ownerDao.getStampsNotAccept(storeId);
    }


    public List<GetCustomerStampResponse> getCustomerStamp(Long ownerId, boolean isCustomerRegular, String sort) {
        log.info("[OwnerService.getCustomerStamp]");

        return ownerDao.getCustomerStamp(ownerId, isCustomerRegular, sort);
    }
}

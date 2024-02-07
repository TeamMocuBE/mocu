package com.example.mocu.Service;

import com.example.mocu.Dao.OwnerDao;
import com.example.mocu.Dao.StoreDao;
import com.example.mocu.Dto.owner.*;
import com.example.mocu.Exception.DatabaseException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.example.mocu.Common.response.status.BaseResponseStatus.DATABASE_ERROR;

@Slf4j
@RestController
@Service
@RequiredArgsConstructor
public class OwnerService {
    private final OwnerDao ownerDao;

    public PostOwnerStoreResponse registerStore(PostOwnerStoreRequest postOwnerStoreRequest) {
        log.info("[OwnerService.registerStore]");

        // TODO 1. Stores table에 정보 저장
        long storeId = ownerDao.registerStore(postOwnerStoreRequest);

        // TODO 2. StoreImage table에 정보 저장
        ownerDao.insertStoreImages(storeId, postOwnerStoreRequest.getStoreImages());

        // TODO 3. Menus table에 정보 저장
        ownerDao.insertMenus(storeId, postOwnerStoreRequest.getMenus());

        return new PostOwnerStoreResponse(storeId);
    }

    public void modifyStoreInfo(PatchOwnerStoreRequest patchOwnerStoreRequest) {
        log.info("[OwnerService.modifyStoreInfo]");

        // TODO 1. Stores table update
        ownerDao.updateStoreInfo(patchOwnerStoreRequest);

        // TODO 2. StoreImage table update
        ownerDao.updateStoreImages(patchOwnerStoreRequest.getStoreId(), patchOwnerStoreRequest.getStoreImages());

        // TODO 3. Menus table update
        ownerDao.updateMenus(patchOwnerStoreRequest.getStoreId(), patchOwnerStoreRequest.getMenus());
    }

    public List<GetOwnerStampNotAcceptResponse> getStampsNotAccept(long storeId) {
        log.info("[OwnerService.getStampsNotAccept]");

        return ownerDao.getStampsNotAccept(storeId);
    }


    public GetOwnerStoreInfoResponse getStoreInfoForOwner(long storeId) {
        log.info("[OwnerService.getStoreInfoForOwner]");

        return ownerDao.getStoreInfoForOwner(storeId);
    }
}

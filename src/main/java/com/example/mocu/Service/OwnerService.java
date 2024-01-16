package com.example.mocu.Service;

import com.example.mocu.Dao.OwnerDao;
import com.example.mocu.Dao.StoreDao;
import com.example.mocu.Dto.owner.PatchOwnerStoreRequest;
import com.example.mocu.Dto.owner.PostOwnerStoreRequest;
import com.example.mocu.Dto.owner.PostOwnerStoreResponse;
import com.example.mocu.Exception.DatabaseException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;

import static com.example.mocu.Common.response.status.BaseResponseStatus.DATABASE_ERROR;

@Slf4j
@RestController
@Service
@RequiredArgsConstructor
public class OwnerService {
    private final OwnerDao ownerDao;
    private final StoreDao storeDao;

    public PostOwnerStoreResponse registerStore(PostOwnerStoreRequest postOwnerStoreRequest) {
        log.info("[OwnerService.registerStore]");

        long storeId = ownerDao.registerStore(postOwnerStoreRequest);
        return new PostOwnerStoreResponse(storeId);
    }

    public void modifyStoreInfo(long storeId, PatchOwnerStoreRequest patchOwnerStoreRequest) {
        log.info("[OwnerService.modifyStoreInfo]");

        ownerDao.modifyStoreInfo(storeId, patchOwnerStoreRequest);
    }


    /*
    public PutStampResponse registerStamp(PutStampRequest putStampRequest) {
        log.info("[StoreService.registerStamp]");

        long StampId;
        // TODO 1. 스탬프 적립이 처음이면 스탬프 create, 아니면 스탬프 add
        if(!storeDao.isNotFirstStamp(putStampRequest.getStoreId(), putStampRequest.getUserId())){
            StampId = return ownerDao.createStamp(putStampRequest);
        }
        return ownerDao.addStamp(putStampRequest);
    }

     */

}

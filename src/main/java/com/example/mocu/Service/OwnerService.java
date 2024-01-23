package com.example.mocu.Service;

import com.example.mocu.Dao.OwnerDao;
import com.example.mocu.Dao.StoreDao;
import com.example.mocu.Dto.owner.GetOwnerStampNotAcceptResponse;
import com.example.mocu.Dto.owner.PatchOwnerStoreRequest;
import com.example.mocu.Dto.owner.PostOwnerStoreRequest;
import com.example.mocu.Dto.owner.PostOwnerStoreResponse;
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



}

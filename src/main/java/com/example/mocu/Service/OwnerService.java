package com.example.mocu.Service;

import com.example.mocu.Dao.OwnerDao;
import com.example.mocu.Dao.StoreDao;
import com.example.mocu.Dto.owner.*;
import com.example.mocu.Exception.DatabaseException;
import com.example.mocu.Exception.OwnerException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.example.mocu.Common.response.status.BaseResponseStatus.DATABASE_ERROR;
import static com.example.mocu.Common.response.status.BaseResponseStatus.INVALID_OWNER_USER_REQUEST_VALUE;


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


    public GetOwnerStoreInfoResponse getStoreInfoForOwner(long storeId) {
        log.info("[OwnerService.getStoreInfoForOwner]");

        return ownerDao.getStoreInfoForOwner(storeId);
    }

    public List<GetUserRequestForOwner> getUserRequestListForOwner(long storeId, boolean notAcceptRequest, boolean bothRequest, boolean rewardRequest, boolean stampRequest, int page) {
        log.info("[OwnerService.getUserRequestListForOwner]");

        // TODO 1. notAcceptRequest 체크
        // TODO 2. reward / stamp / both 체크
        if(notAcceptRequest){
            // '수락 안 한 요청만' : true
            if(rewardRequest){
                // '보상만'
                return ownerDao.getNotAcceptedCouponRequests(storeId, page);
            }
            else if(stampRequest){
                // '적립만'
                return ownerDao.getNotAcceptedStampRequests(storeId, page);
            }
            else if(bothRequest){
                // '보상 + 적립 전체'
                return ownerDao.getNotAcceptedBothRequests(storeId, page);
            }
            else{
                // 클라이언트에게 인자 잘못 전달받음 -> 에외처리
                throw new OwnerException(INVALID_OWNER_USER_REQUEST_VALUE);
            }
        }
        else{
            // '수락 안 한 요청만' : false
            if(rewardRequest){
                // '보상만'
                return ownerDao.getAllCouponRequests(storeId, page);
            }
            else if(stampRequest){
                // '적립만'
                return ownerDao.getAllStampRequests(storeId, page);
            }
            else if(bothRequest){
                // '보상 + 적립 전체'
                return ownerDao.getAllBothRequests(storeId, page);
            }
            else{
                // 예외처리
                throw new OwnerException(INVALID_OWNER_USER_REQUEST_VALUE);
            }
        }
    }

    public List<GetCustomerStampResponse> getCustomerStamp(Long ownerId, boolean isCustomerRegular, String sort) {
        log.info("[OwnerService.getCustomerStamp]");

        return ownerDao.getCustomerStamp(ownerId, isCustomerRegular, sort);
    }
}

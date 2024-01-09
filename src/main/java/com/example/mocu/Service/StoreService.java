package com.example.mocu.Service;

import com.example.mocu.Common.exception.StoreException;
import com.example.mocu.Dao.StoreDao;
import com.example.mocu.Dto.store.GetDetailedStoreResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import static com.example.mocu.Common.response.status.BaseResponseStatus.STORE_NOT_FOUND;

@Service
@Slf4j
@RequiredArgsConstructor
public class StoreService {
    private final StoreDao storeDao;
    public GetDetailedStoreResponse getDetailedStore(long storeId) {
        log.info("[StoreService.getDetailed]");

        try{
            return storeDao.getDetailedStore(storeId);
        } catch (EmptyResultDataAccessException e){
            throw new StoreException(STORE_NOT_FOUND);
        }
    }
}

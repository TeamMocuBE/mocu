package com.example.mocu.Service;

import com.example.mocu.Dao.MapDao;
import com.example.mocu.Dto.map.GetMapStoreInfoResponse;
import com.example.mocu.Dto.map.GetMapStoreResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@Service
@RequiredArgsConstructor
public class MapService {
    private final MapDao mapDao;

    public List<GetMapStoreResponse> getMapStoreList(long userId, double latitude, double longitude, String categoryOption, boolean eventOption, boolean dueDateOption) {
        log.info("[MapService.getMapStoreInfoList]");

        // TODO 1. 주어진 위도, 경도를 기준으로 반경 distance 미터 내의 GetMapStoreResponse값들을 return
        // 1km로 설정
        int distance = 1000000;
        return mapDao.getMapStoreList(userId, latitude, longitude, distance, categoryOption, eventOption, dueDateOption);
    }

    public GetMapStoreInfoResponse getMapStoreInfo(long userId, long storeId) {
        log.info("[MapService.getMapStoreInfo]");

        return mapDao.getMapStoreInfo(userId, storeId);
    }
}

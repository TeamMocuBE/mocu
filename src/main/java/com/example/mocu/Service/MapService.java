package com.example.mocu.Service;

import com.example.mocu.Dao.MapDao;
import com.example.mocu.Dto.map.GetMapStoreInfoResponse;
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

    public List<GetMapStoreInfoResponse> getMapStoreInfoList(long userId, double latitude, double longitude) {
        log.info("[MapService.getMapStoreInfoList]");

        // TODO . user의 현위치를 기준으로 반경 distance 미터 내의 GetMapStoreInfoResponse값들을 return
        int distance = 1000;
        return mapDao.getMapStoreInfoList(userId, latitude, longitude, distance);
    }
}

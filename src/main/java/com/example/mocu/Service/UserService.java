package com.example.mocu.Service;

import com.example.mocu.Dao.MissionDao;
import com.example.mocu.Dao.UserDao;
import com.example.mocu.Dto.user.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserDao userDao;
    private final MissionDao missionDao;

    public List<GetUserResponse> getUsers(String name, String email, String status) {
        log.info("[UserService.getUsers]");
        return userDao.getUsers(name, email, status);
    }

    public GetMyPageResponse getMypage(Long userId) {
        log.info(("[UserService.getMypage]"));
        return userDao.getMypage(userId);
    }

    public PatchUserRegularResponse handleRegularRequest(PatchUserRegularRequest patchUserRegularRequest) {
        log.info("[UserService.handleRegularRequest]");

        // TODO 1. Regular table에서 tuple의 status update
        userDao.updateRegularStatus(patchUserRegularRequest);

        // TODO 2. '단골 맺기' 가 오늘의 미션에 해당되는지 & '단골 맺기'의 상태가 'accept' 인지 체크
        // 오늘의 미션 중 '단골 맺기' 가 있는지 &  체크
        boolean isTodayMission = false;
        if(missionDao.isTodayMissionAssigned(patchUserRegularRequest.getUserId(), "단골 맺기") &&
        userDao.isRegular(patchUserRegularRequest.getUserId(), patchUserRegularRequest.getStoreId())){
            isTodayMission = true;
        }

        // TODO 3. TODO 2 통과할 경우 '미션 완료' 처리
        if(isTodayMission){
            missionDao.updateTodayMissionToDone(patchUserRegularRequest.getUserId());
        }

        // TODO 4. RETURN
        long regularId= userDao.getRegularId(patchUserRegularRequest.getUserId(), patchUserRegularRequest.getStoreId());
        return new PatchUserRegularResponse(regularId, isTodayMission);
    }

    public GetMyStoreListResponse getMyStoreList(long userId, String category, String sort) {
        log.info("[UserService.getMyStoreList]");

        List<GetRegularResponse> storeList = userDao.getMyStoreList(userId, category, sort);

        int regularsCount = userDao.getRegularsCount(userId);

        return new GetMyStoreListResponse(regularsCount, storeList);
    }
}

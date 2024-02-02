package com.example.mocu.Service;

import com.example.mocu.Dao.MissionDao;
import com.example.mocu.Dao.UserDao;
import com.example.mocu.Dto.user.GetMyPageResponse;
import com.example.mocu.Dto.user.GetUserResponse;
import com.example.mocu.Dto.user.PostUserRegularRequest;
import com.example.mocu.Dto.user.PostUserRegularResponse;
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

    public PostUserRegularResponse handleRegularRequest(PostUserRegularRequest postUserRegularRequest) {
        log.info("[UserService.handleRegularRequest]");

        // TODO 1. Regular table에 tuple insert
        long regularId = userDao.handleRegularRequest(postUserRegularRequest);

        // TODO 2. '단골 맺기' 가 오늘의 미션에 해당되는지 & '단골 맺기'의 상태가 'accept' 인지 체크
        // 오늘의 미션 중 '단골 맺기' 가 있는지 &  체크
        boolean isTodayMission = false;
        if(missionDao.isTodayMissionAssigned(postUserRegularRequest.getUserId(), "단골 맺기") &&
        userDao.isRegular(regularId)){
            isTodayMission = true;
        }

        // TODO 3. TODO 2 통과할 경우 '미션 완료' 처리
        if(isTodayMission){
            missionDao.updateTodayMissionToDone(postUserRegularRequest.getUserId());
        }

        // TODO 4. RETURN
        return new PostUserRegularResponse(regularId, isTodayMission);
    }
}

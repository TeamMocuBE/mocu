package com.example.mocu.Service;

import com.example.mocu.Dao.MissionDao;
import com.example.mocu.Dao.UserDao;
import com.example.mocu.Dto.mission.*;
import com.example.mocu.Exception.MissionException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.example.mocu.Common.response.status.BaseResponseStatus.IS_NOT_DONE;

@Slf4j
@RestController
@Service
@RequiredArgsConstructor
public class MissionService {
    private final MissionDao missionDao;
    private final UserDao userDao;

    public List<GetTodayMissionResponse> getTodayMissionsForUser(long userId) {
        log.info("[MissionService.getTodayMissionsForUser]");

        return missionDao.getTodayMissionsForUser(userId);
    }

    /**
     * 매일 자정마다 미션 목록 중 오늘의 미션 2개를 선택
     * @Scheduled(cron = "0 0 0 * * ?")는 매일 자정에 실행되도록 스케줄링됩니다.
     */
    @Scheduled(cron = "0 0 0 * * ?")
    public void updateTodayMissions(){
        log.info("[MissionService.updateTodayMissions]");

        // TODO 1. 'MOCU앱 출석하기' 미션을 제외한 모든 MISSIONS TABLE의 TUPLE들 상태를 "not-select"으로 update
        missionDao.updateAllMissionsStatusWithoutAttendanceMission();

        // TODO 2. 'MOCU 앱 출석하기' 를 제외한 2개의 TUPLE들을 랜덤으로 골라서 STATUS를 "select"로 update
        List<Long> selectedMissionIds = missionDao.getRandomMissionIds(2);
        missionDao.updateMissionsStatusToSelect(selectedMissionIds);

        // TODO 3. Users table의 모든 유저들의 userId 목록 조회
        List<Long> userIds = userDao.getAllUserIds();

        // TODO 4. 모든 user 들의 오늘의 미션 목록 update
        for(Long userId : userIds){
            missionDao.updateTodayMissionsForUser(userId, selectedMissionIds);
        }
    }


    public GetMissionMapResponse getMissionMapForUser(long userId) {
        log.info("[MissionService.getMissionMapForUser]");

        // TODO 1. 해당 userId값을 가지는 tuple이 MissionStamps table내에 존재하는지 체크
        // 없으면 해당 userId값을 가지는 tuple을 MissionStamps table에 insert
        if(missionDao.isNotThereMissionMapForThatUser(userId)){
            missionDao.insertMissionMap(userId);
        }

        return missionDao.getMissionMapForUser(userId);
    }

    public PatchMissionMapCompleteResponse completeMissionMap(PatchMissionMapCompleteRequest patchMissionMapCompleteRequest) {
        log.info("[MissionService.completeMissionMap]");

        // TODO 1. MissionStamps table의 status 값을 "done" 으로 변경
        missionDao.updateMissionMapStatusToDone(patchMissionMapCompleteRequest.getUserId());

        // TODO 2. reward 조회
        String reward = missionDao.getRewardForMissionMap(patchMissionMapCompleteRequest.getUserId());

        return new PatchMissionMapCompleteResponse(reward);
    }

    /**
     * 매달 첫째날 00시에 미션맵 update
     * the method will be scheduled to run at 00:00 (midnight) on the first day of every month.
     */
    @Scheduled(cron = "0 0 0 1 * ?")
    public void updateMissionMap() {
        log.info("[MissionService.updateMissionMap]");

        // TODO 1. Users table의 모든 유저들의 userId 목록 조회
        List<Long> userIds = userDao.getAllUserIds();

        // TODO 2. 모든 USER 들의 미션 맵 update
        for(Long userId : userIds){
            missionDao.updateMissionMapForUser(userId);
        }
    }

    public PatchTodayMissionDoneResponse todayMissionDone(PatchTodayMissionDoneRequest patchTodayMissionDoneRequest) {
        log.info("[MissionService.todayMissionDone]");

        // TODO 1. 해당 userId값을 가지는 tuple이 MissionStamps table내에 존재하는지 체크
        // 없으면 해당 userId값을 가지는 tuple을 MissionStamps table에 insert
        if(missionDao.isNotThereMissionMapForThatUser(patchTodayMissionDoneRequest.getUserId())){
            missionDao.insertMissionMap(patchTodayMissionDoneRequest.getUserId());
        }

        // TODO 2. 해당 todayMission의 status가 'done'인지 체크
        boolean isThatDone = missionDao.isTodayMissionPerformed(patchTodayMissionDoneRequest.getTodayMissionId());
        if(!isThatDone){
            throw new MissionException(IS_NOT_DONE);
        }

        // TODO 3. TODO 1 통과할 경우 TodayMissions table에서 해당 userId값을 가진 tuple들의 status값이 'stamped'인 tuple의 개수를 count
        int stampedTodayMission = missionDao.getTodayMissionStamped(patchTodayMissionDoneRequest.getUserId());

        // TODO 4. TODO 2 의 결과가 1 이하이면, TodayMissions table에서 해당 todayMissionId값을 가진 tuple의 status값을 'stamped'로 변경 & MissionStamps table에서 numOfStamp 값을 1을 더한 값으로 update & Missions table에서 content값을 가져오기
        // TODO 2 의 결과가 2 이상이면, "이미 2개의 미션 스탬프를 획득하였습니다." 를 PatchTodayMissionDoneResponse 형식에 맞춰서 return
        if(stampedTodayMission <= 1){
            missionDao.updateTodayMissionToStamped(patchTodayMissionDoneRequest.getTodayMissionId());
            missionDao.updateNumOfMissionStamp(patchTodayMissionDoneRequest.getUserId());
            // 미션 스탬프 개수가 30개에 도달했는지 체크해야 함
            boolean isPossibleGetReward = missionDao.isPossibleGetReward(patchTodayMissionDoneRequest.getUserId());

            return new PatchTodayMissionDoneResponse(missionDao.getTodayMissionContent(patchTodayMissionDoneRequest.getTodayMissionId()), isPossibleGetReward);
        }
        else{
            return new PatchTodayMissionDoneResponse("이미 2개의 미션 스탬프를 획득하였습니다.", false);
        }
    }





}

package com.example.mocu.Dao;

import com.example.mocu.Dto.mission.GetMissionMapResponse;
import com.example.mocu.Dto.mission.GetTodayMissionResponse;
import com.example.mocu.Dto.mission.PatchMissionMapCompleteRequest;
import com.example.mocu.Dto.mission.PatchMissionMapCompleteResponse;
import com.example.mocu.MocuApplication;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

@Slf4j
@Repository
public class MissionDao {
    private final NamedParameterJdbcTemplate jdbcTemplate;

    public MissionDao(DataSource dataSource){
        this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }
    public List<GetTodayMissionResponse> getTodayMissionsForUser(long userId) {
        String sql = "select tm.todayMissionId, m.content, tm.status from TodayMissions tm join Missions m on tm.missionId=m.missionId " +
                "where tm.userId=:userId";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("userId", userId);

        return jdbcTemplate.query(sql, params, new BeanPropertyRowMapper<>(GetTodayMissionResponse.class));
    }

    public void updateAllMissionsToNotSelect() {
        String sql = "update Missions set status='not-select' where content!='MOCU앱 출석하기'";
        jdbcTemplate.update(sql, new MapSqlParameterSource());
    }

    public void updateAttendanceMissionToSelect() {
        String sql = "update Missions set status='select' where content='MOCU앱 출석하기'";
        jdbcTemplate.update(sql, new MapSqlParameterSource());
    }

    public List<Long> getRandomMissionIds(int count) {
        String sql = "select missionId from Missions where content!='MOCU앱 출석하기' order by rand() limit :count";

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("count", count);

        return jdbcTemplate.queryForList(sql, params, Long.class);
    }


    public void updateMissionsStatusToSelect(List<Long> selectedMissionIds) {
        String sql = "update Missions set status='select' where missionId in (:selectedMissionIds)";

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("selectedMissionIds", selectedMissionIds);

        jdbcTemplate.update(sql, params);
    }

    public long getAttendanceMissionId() {
        String sql = "select missionId from Missions where content='MOCU앱 출석하기'";
        return jdbcTemplate.queryForObject(sql, new MapSqlParameterSource(), Long.class);
    }

    public void updateTodayMissionsForUser(Long userId, List<Long> selectedMissionIds) {
        // TODO 1. User들이 가지고 있는 오늘의 미션 목록 delete
        String sql = "delete from TodayMissions where userId=:userId";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("userId", userId);
        jdbcTemplate.update(sql, params);

        // TODO 2. 새로 update된 오늘의 미션 목록을 insert
        String insertSql = "insert into TodayMissions (userId, missionId) values (:userId, :missionId)";
        MapSqlParameterSource insertParams = new MapSqlParameterSource();
        insertParams.addValue("userId", userId);
        for(Long missionId : selectedMissionIds){
            insertParams.addValue("missionId", missionId);
            jdbcTemplate.update(insertSql, insertParams);
        }
    }


    public boolean isTodayMissionAssigned(long userId, String content) {
        String sql = "select count(*) from TodayMissions tm join Missions m on tm.missionId=m.missionId " +
                "where tm.userId=:userId and m.content=:content";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("userId", userId);
        params.addValue("content", content);

        return jdbcTemplate.queryForObject(sql, params, Integer.class) > 0;
    }

    public void updateTodayMissionToDone(long todayMissionId) {
        String sql = "update TodayMissions set status='done' where todayMissionId=:todayMissionId";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("todayMissionId", todayMissionId);

        jdbcTemplate.update(sql, params);
    }


    public boolean isNotThereMissionMapForThatUser(long userId) {
        String sql = "select count(*) from MissionStamps where userId=:userId";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("userId", userId);
        return jdbcTemplate.queryForObject(sql, params, Integer.class) == 0;
    }

    public void insertMissionMap(long userId) {
        String sql = "insert into MissionStamps (userId, reward) values (:userId, :reward)";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("userId", userId);
        params.addValue("reward", "스타벅스 2만원권");

        jdbcTemplate.update(sql, params);
    }

    public GetMissionMapResponse getMissionMapForUser(long userId) {
        String sql = "select numOfStamp, reward, createdDate, status from MissionStamps where userId=:userId";
        MapSqlParameterSource params = new MapSqlParameterSource();

        return jdbcTemplate.queryForObject(sql, params, (rs, rowNum) ->
                new GetMissionMapResponse(
                        rs.getInt("numOfStamp"),
                        rs.getString("reward"),
                        timestampToString(rs.getTimestamp("createdDate")),
                        rs.getString("status")
                )
        );
    }

    private String timestampToString(Timestamp timestamp) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        return (timestamp != null) ? dateFormat.format(timestamp) : null;
    }

    public void updateMissionMapStatusToDone(long userId) {
        String sql = "update MissionStamps set status='done' where userId=:userId";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("userId", userId);
        jdbcTemplate.update(sql, params);
    }

    public String getRewardForMissionMap(long userId) {
        String sql = "select reward from MissionStamps where userId=:userId";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("userId", userId);

        return jdbcTemplate.queryForObject(sql, params, String.class);
    }


    public void updateMissionMapForUser(Long userId) {
        String sql = "update MissionStamps set numOfStamp=0, status='not-done' where userId=:userId";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("userId", userId);
        jdbcTemplate.update(sql, params);
    }

    public boolean isTodayMissionPerformed(long todayMissionId) {
        String sql = "select status from TodayMissions where todayMissionId=:todayMissionId";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("todayMissionId", todayMissionId);
        String status = jdbcTemplate.queryForObject(sql, params, String.class);
        return "done".equals(status);
    }

    public int getTodayMissionStamped(long userId) {
        String sql = "select count(*) from TodayMissions where userId=:userId and status='stamped'";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("userId", userId);
        return jdbcTemplate.queryForObject(sql, params, Integer.class);
    }

    public void updateTodayMissionToStamped(long todayMissionId) {
        String sql = "update TodayMissions set status='stamped' where todayMissionId=:todayMissionId";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("todayMissionId", todayMissionId);
        jdbcTemplate.update(sql, params);
    }

    public void updateNumOfMissionStamp(long userId) {
        String sql = "update MissionStamps set numOfStamp=numOfStamp+1 where userId=:userId";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("userId", userId);
        jdbcTemplate.update(sql, params);
    }

    public String getTodayMissionContent(long todayMissionId) {
        String sql = "select content from Missions m join TodayMissions tm on m.missionId=tm.missionId where tm.todayMissionId=:todayMissionId";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("todayMissionId", todayMissionId);
        return jdbcTemplate.queryForObject(sql, params, String.class);
    }

    public boolean isPossibleGetReward(long userId) {
        String sql = "select numOfStamp from MissionStamps where userId=:userId";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("userId", userId);

        // 미션 맵 보상 얻을 수 있는 최소 미션 스탬프 개수
        int stampThreshold = 30;

        Integer numOfStamp = jdbcTemplate.queryForObject(sql, params, Integer.class);

        return numOfStamp != null && numOfStamp >= stampThreshold;

    }

    public long getTodayMissionId(long userId, String content) {
        String sql = "select todayMissionId from Missions m join TodayMissions tm on m.missionId=tm.missionId " +
                "where tm.userId=:userId and m.content=:content";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("userId", userId);
        params.addValue("content", content);

        return jdbcTemplate.queryForObject(sql, params, long.class);
    }



}

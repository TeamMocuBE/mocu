package com.example.mocu.Dao;

import com.example.mocu.Dto.mission.GetMissionMapResponse;
import com.example.mocu.Dto.mission.GetTodayMissionResponse;
import com.example.mocu.Dto.mission.PatchMissionMapCompleteRequest;
import com.example.mocu.Dto.mission.PatchMissionMapCompleteResponse;
import com.example.mocu.MocuApplication;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
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
        String sql = "select m.content, tm.status from TodayMissions tm join Missions m on tm.missionId=m.missionId " +
                "where tm.userId=:userId";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("userId", userId);

        return jdbcTemplate.query(sql, params, (rs, rowNum) ->
                new GetTodayMissionResponse(
                        rs.getString("content"),
                        rs.getString("status")
                )
        );
    }

    public void updateAllmissionsStatusWithoutAttendanceMission() {
        String sql = "update Missions set status='not-select' where content!='MOCU앱 출석하기'";
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

    public int getTodayMissionPerformed(long userId) {
        String sql = "select count(*) from TodayMissions where userId=:userId and status='done'";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("userId", userId);

        return jdbcTemplate.queryForObject(sql, params, Integer.class);
    }

    public void updateTodayMissionToDone(long userId) {
        String sql = "update TodayMissions set status='done' where userId=:userId";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("userId", userId);

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
        String sql = "select numOfStamp, reward, createdDate, complete from MissionStamps where userId=:userId";
        MapSqlParameterSource params = new MapSqlParameterSource();

        return jdbcTemplate.queryForObject(sql, params, (rs, rowNum) ->
                new GetMissionMapResponse(
                        rs.getInt("numOfStamp"),
                        rs.getString("reward"),
                        timestampToString(rs.getTimestamp("createdDate")),
                        rs.getBoolean("complete")
                )
        );
    }

    private String timestampToString(Timestamp timestamp) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        return (timestamp != null) ? dateFormat.format(timestamp) : null;
    }

    public void updateMissionMapToComplete(long userId) {
        String sql = "update MissionStamps set complete=true where userId=:userId";
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
        String sql = "update MissionStamps set numOfStamp=0, complete=false where userId=:userId";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("userId", userId);
        jdbcTemplate.update(sql, params);
    }
}

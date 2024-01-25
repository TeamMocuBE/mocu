package com.example.mocu.Dao;

import com.example.mocu.Dto.mission.GetTodayMissionResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

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

    public void updateAllmissionsStatus() {
        String sql = "update Missions set status='not-select'";
        jdbcTemplate.update(sql, new MapSqlParameterSource());
    }

    public List<Long> getRandomMissionIds(int count) {
        String sql = "select missionId from Missions order by rand() limit :count";

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("count", count);

        return jdbcTemplate.queryForList(sql, params, Long.class);
    }

    public void updateMissionsStatusToSelect(List<Long> selectedMissionIds) {
        String sql = "update Missions set status=:'select' where missionId in (:selectedMissionIds)";

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
}

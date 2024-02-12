package com.example.mocu.Dao;

import com.example.mocu.Dto.map.GetMapStoreInfoResponse;
import com.example.mocu.Dto.map.GetMapStoreResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Slf4j
@Repository
public class MapDao {
    private final NamedParameterJdbcTemplate jdbcTemplate;

    public MapDao(DataSource dataSource){
        this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

    public List<GetMapStoreResponse> getMapStoreList(long userId, double latitude, double longitude, int distance, String categoryOption, boolean eventOption, boolean dueDateOption) {
        // TODO 1. stampId값이 존재하는지 체크
        String checkSql = "SELECT COUNT(*) FROM Stamps st " +
                "INNER JOIN Stores s ON st.storeId = s.storeId " +
                "WHERE ST_DISTANCE_SPHERE(POINT(:userLongitude, :userLatitude), POINT(s.longitude, s.latitude)) <= :distance " +
                "AND st.userId = :userId";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("userLongitude", longitude);
        params.addValue("userLatitude", latitude);
        params.addValue("distance", distance);
        params.addValue("userId", userId);

        int count = jdbcTemplate.queryForObject(checkSql, params, Integer.class);

        if(count > 0){
            // TODO 2. stampId값이 존재하는 경우
            String sql = "select s.storeId, s.latitude as storeLatitude, s.longitude as storeLongitude, s.category, " +
                    "case when s.event IS NULL THEN false ELSE true END as hasEvent, st.dueDate as isDueDate " +
                    "from Stores s join Stamps st on s.storeId=st.storeId " +
                    "WHERE ST_DISTANCE_SPHERE(POINT(:userLongitude, :userLatitude), POINT(s.longitude, s.latitude)) <= :distance ";

            // 1. categoryOption에 따라 카테고리 필터링
            if (!categoryOption.equals("업종 전체")) {
                sql += "AND s.category = :categoryOption ";
                params.addValue("categoryOption", categoryOption);
            }

            // 2. eventOption에 따라 이벤트 여부 필터링
            if (eventOption) {
                sql += "AND s.event IS NOT NULL ";
            }

            // 3. dueDateOption에 따라 쿠폰사용 임박 여부 필터링
            if(dueDateOption){
                sql += "and st.dueDate is true ";
            }

            sql += "ORDER BY ST_DISTANCE_SPHERE(POINT(:userLongitude, :userLatitude), POINT(s.longitude, s.latitude))";

            return jdbcTemplate.query(sql, params, (rs, rowNum) ->
                    new GetMapStoreResponse(
                            rs.getLong("storeId"),
                            rs.getDouble("storeLatitude"),
                            rs.getDouble("storeLongitude"),
                            rs.getString("category"),
                            rs.getBoolean("hasEvent"),
                            rs.getBoolean("isDueDate")
                    )
            );

        }
        else{
            // TODO 3. stampId값이 존재하지 않는 경우
            String sql = "select s.storeId, s.latitude as storeLatitude, s.longitude as storeLongitude, s.category, " +
                    "case when s.event IS NULL THEN false ELSE true END as hasEvent " +
                    "from Stores s " +
                    "WHERE ST_DISTANCE_SPHERE(POINT(:userLongitude, :userLatitude), POINT(s.longitude, s.latitude)) <= :distance ";

            // 1. categoryOption에 따라 카테고리 필터링
            if (!categoryOption.equals("업종 전체")) {
                sql += "AND s.category = :categoryOption ";
                params.addValue("categoryOption", categoryOption);
            }

            // 2. eventOption에 따라 이벤트 여부 필터링
            if (eventOption) {
                sql += "AND s.event IS NOT NULL ";
            }

            sql += "ORDER BY ST_DISTANCE_SPHERE(POINT(:userLongitude, :userLatitude), POINT(s.longitude, s.latitude))";

            return jdbcTemplate.query(sql, params, (rs, rowNum) ->
                    new GetMapStoreResponse(
                            rs.getLong("storeId"),
                            rs.getDouble("storeLatitude"),
                            rs.getDouble("storeLongitude"),
                            rs.getString("category"),
                            rs.getBoolean("hasEvent"),
                            false
                    )
            );
        }
    }

    public GetMapStoreInfoResponse getMapStoreInfo(long userId, long storeId) {
        // TODO 1. stampId값이 존재하는지 체크
        String checkSql = "SELECT COUNT(*) FROM Stamps st " +
                "INNER JOIN Stores s ON st.storeId = s.storeId " +
                "WHERE st.userId = :userId";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("userId", userId);

        int count = jdbcTemplate.queryForObject(checkSql, params, Integer.class);

        if(count == 0){
            // TODO 2. stampId값이 존재하지 않는 경우 stampId 생성
            createStampId(userId, storeId);
        }

        // TODO 3. return해야할 값 찾아서 return
        String sql = "select s.name as storeName, s.mainImageUrl, s.category, st.dueDate, s.rating, st.numOfStamp, " +
                "s.maxStamp, st.numOfCouponAvailable, s.reward, s.event " +
                "from Stores s join Stamps st on s.storeId=st.storeId and s.storeId=:storeId " +
                "where st.userId=:userId";
        params.addValue("storeId", storeId);

        return jdbcTemplate.queryForObject(sql, params, (rs, rowNum) ->
                new GetMapStoreInfoResponse(
                        rs.getString("storeName"),
                        rs.getString("mainImageUrl"),
                        rs.getString("category"),
                        rs.getBoolean("dueDate"),
                        rs.getDouble("rating"),
                        rs.getInt("numOfStamp"),
                        rs.getInt("maxStamp"),
                        rs.getInt("numOfCouponAvailable"),
                        rs.getString("reward"),
                        rs.getString("event")
                )
        );
    }

    private void createStampId(long userId, long storeId) {
        String sql = "insert into Stamps (userId, storeId) values (:userId, :storeId)";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("userId", userId);
        params.addValue("storeId", storeId);

        jdbcTemplate.update(sql, params);
    }

}

package com.example.mocu.Dao;

import com.example.mocu.Dto.map.GetMapStoreInfoResponse;
import com.example.mocu.Dto.map.GetMapStoreResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Repository
public class MapDao {
    private final NamedParameterJdbcTemplate jdbcTemplate;

    public MapDao(DataSource dataSource){
        this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

    public List<GetMapStoreResponse> getMapStoreList(long userId, double latitude, double longitude, int distance, String categoryOption, boolean eventOption, boolean dueDateOption) {
        log.info("[MapDao.getMapStoreList]");

        // TODO 1. 해당 유저 근방 가게의 storeId get
        log.info("todo1");
        String sql = "select storeId from Stores s where ST_DISTANCE_SPHERE(POINT(:userLongitude, :userLatitude), POINT(s.longitude, s.latitude)) <= :distance";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("userLongitude", longitude);
        params.addValue("userLatitude", latitude);
        params.addValue("distance", distance);

        List<Long> storeIds = jdbcTemplate.queryForList(sql, params, Long.class);
        List<GetMapStoreResponse> responseList = new ArrayList<>();

        // TODO 2. 각 storeId마다 해당 유저가 적립한 적이 있는 가게인지 체크
        log.info("todo2");
        for(long storeId : storeIds){
            if(!hasStampId(userId, storeId)){
                log.info("stampId 생성");
                createStampId(userId, storeId);
            }
            // TODO 3. GetMapStoreResponse 에 맞는 값 select
            log.info("todo3");
            String selectSql = "select s.storeId, s.latitude, s.longitude, s.category, " +
                    "case when s.event is null then false else true end as hasEvent, st.dueDate as isDueDate " +
                    "from Stores s join Stamps st on s.storeId=st.storeId " +
                    "where s.storeId=:storeId and st.userId=:userId ";
            MapSqlParameterSource selectParam = new MapSqlParameterSource();
            selectParam.addValue("storeId", storeId);
            selectParam.addValue("userId", userId);

            // TODO 4. 필터링
            log.info("todo4");
            // 1. categoryOption에 따라 카테고리 필터링
            if (!categoryOption.equals("업종 전체")) {
                selectSql += "AND s.category = :categoryOption ";
                selectParam.addValue("categoryOption", categoryOption);
            }

            // 2. eventOption에 따라 이벤트 여부 필터링
            if (eventOption) {
                selectSql += "AND s.event IS NOT NULL ";
            }

            // 3. dueDateOption에 따라 쿠폰사용 임박 여부 필터링
            if(dueDateOption){
                selectSql += "and st.dueDate is true ";
            }
            selectSql += "ORDER BY ST_DISTANCE_SPHERE(POINT(:userLongitude, :userLatitude), POINT(s.longitude, s.latitude))";
            selectParam.addValue("userLongitude", longitude);
            selectParam.addValue("userLatitude", latitude);

            GetMapStoreResponse response = jdbcTemplate.queryForObject(selectSql, selectParam, (rs, rowNum) ->
                    new GetMapStoreResponse(
                            rs.getLong("storeId"),
                            rs.getDouble("latitude"),
                            rs.getDouble("longitude"),
                            rs.getString("category"),
                            rs.getBoolean("hasEvent"),
                            rs.getBoolean("isDueDate")
                    )
            );

            responseList.add(response);
        }

        // TODO 5. return
        return responseList;
    }

    private boolean hasStampId(long userId, long storeId) {
        String sql = "select count(*) from Stamps st join Stores s on st.storeId=s.storeId " +
                "where st.storeId=:storeId and userId=:userId";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("storeId", storeId);
        params.addValue("userId", userId);

        int count = jdbcTemplate.queryForObject(sql, params, Integer.class);
        return count > 0;
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

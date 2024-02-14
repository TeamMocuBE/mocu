package com.example.mocu.Dao;

import com.example.mocu.Dto.store.RecommendStoreInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Repository
public class RecommendDao {
    private final NamedParameterJdbcTemplate jdbcTemplate;

    public RecommendDao(DataSource dataSource){
        this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }


    public List<RecommendStoreInfo> getRecommendStoreInfoListForNewUser(double latitude, double longitude, int recommendLimit) {
        log.info("[RecommendDao.getRecommendStoreInfoListForNewUser]");

        // TODO 1. 해당 유저 근방 가게의 storeId get
        log.info("todo1");

        int distance = 1000;            // 1km
        String sql = "select storeId from Stores s where ST_DISTANCE_SPHERE(POINT(:userLongitude, :userLatitude), POINT(s.longitude, s.latitude)) <= :distance";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("userLongitude", longitude);
        params.addValue("userLatitude", latitude);
        params.addValue("distance", distance);

        List<Long> storeIds = jdbcTemplate.queryForList(sql, params, Long.class);

        // TODO 2. TODO 1 에서 걸러낸 가게들 중 평점이 높은 가게들을 return
        log.info("todo2");

        String inClausePlaceholders = storeIds.stream().map(id -> ":storeId_" + id).collect(Collectors.joining(", "));
        String selectSql = "select name as storeName, " +
                "case when event is null then false else true end as hasEvent, mainImageUrl," +
                "ST_DISTANCE_SPHERE(POINT(:userLongitude, :userLatitude), POINT(longitude, latitude)) as distance " +
                "from Stores where storeId in (" + inClausePlaceholders + ") " +
                "order by rating desc limit :recommendLimit";
        MapSqlParameterSource selectParams = new MapSqlParameterSource();
        selectParams.addValue("userLongitude", longitude);
        selectParams.addValue("userLatitude", latitude);
        selectParams.addValue("recommendLimit", recommendLimit);
        for(Long id : storeIds){
            selectParams.addValue("storeId_" + id, id);
        }

        List<RecommendStoreInfo> recommendStoreInfoList = jdbcTemplate.query(selectSql, selectParams, (rs, rowNum) -> {
            RecommendStoreInfo recommendStoreInfo = new RecommendStoreInfo();
            recommendStoreInfo.setStoreName(rs.getString("storeName"));
            recommendStoreInfo.setHasEvent(rs.getBoolean("hasEvent"));
            recommendStoreInfo.setMainImageUrl(rs.getString("mainImageUrl"));
            recommendStoreInfo.setDistance(rs.getDouble("distance"));
            return recommendStoreInfo;
        });

        // Return an empty list if the result is empty
        return recommendStoreInfoList.isEmpty() ? Collections.singletonList(new RecommendStoreInfo()) : recommendStoreInfoList;
    }

    public List<String> getFavoriteCategories(List<Long> storeIds, long userId, int numOfCategory) {
        log.info("[RecommendDao.getFavoriteCategories]");

        String inClausePlaceholders = storeIds.stream().map(id -> ":storeId_" + id).collect(Collectors.joining(", "));
        String sql = "select s.category from Stores s join Stamps st on s.storeId=st.storeId " +
                "where st.userId=:userId and st.status='active' and " +
                "s.storeId in (" + inClausePlaceholders + ") " +
                "group by s.category order by max(st.modifiedDate) desc limit :numOfCategory";

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("userId", userId);
        params.addValue("numOfCategory", numOfCategory);
        for(Long id : storeIds){
            params.addValue("storeId_" + id, id);
        }

        return jdbcTemplate.queryForList(sql, params, String.class);
    }

    public RecommendStoreInfo getRecommendStoreInfo(String category, double latitude, double longitude, int recommendLimit) {
        log.info("[RecommendDao.getRecommendStoreInfo]");

        String sql = "select s.name as storeName, " +
                "case when s.event is null then false else true end as hasEvent, " +
                "s.mainImageUrl, " +
                "ST_DISTANCE_SPHERE(POINT(:userLongitude, :userLatitude), POINT(s.longitude, s.latitude)) as distance " +
                "from Stores s where s.category=:category " +
                "order by s.rating desc limit :recommendLimit";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("userLongitude", longitude);
        params.addValue("userLatitude", latitude);
        params.addValue("category", category);
        params.addValue("recommendLimit", recommendLimit);

        return jdbcTemplate.queryForObject(sql, params, (rs, rowNum) ->
                new RecommendStoreInfo(
                        rs.getString("storeName"),
                        rs.getBoolean("hasEvent"),
                        rs.getString("mainImageUrl"),
                        rs.getDouble("distance")
                )
        );
    }
}

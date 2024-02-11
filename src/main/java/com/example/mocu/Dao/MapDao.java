package com.example.mocu.Dao;

import com.example.mocu.Dto.map.GetMapStoreInfoResponse;
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

    public List<GetMapStoreInfoResponse> getMapStoreInfoList(double latitude, double longitude, int distance, String categoryOption, boolean eventOption, boolean dueDateOption) {
        // ST_DISTANCE_SPHERE 함수 이용
        String sql = "select s.name as storeName, s.mainImageUrl, s.category, st.dueDate, s.rating, " +
                "st.numOfStamp, s.maxStamp, st.numOfCouponAvailable, s.reward, s.event" +
                "from Stores s join Stamps st on s.storeId=st.storeId " +
                "where ST_DISTANCE_SPHERE(POINT(:userLongitude, :userLatitude), POINT(s.longitude, s.latitude)) <= :distance ";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("userLongitude", longitude);
        params.addValue("userLatitude", latitude);
        params.addValue("distance", distance);

        // TODO 1. categoryOption에 따라 카테고리 필터링
        if(!categoryOption.equals("업종 전체")){
            sql += "and s.category=:categoryOption ";
            params.addValue("categoryOption", categoryOption);
        }

        // TODO 2. eventOption에 따라 이벤트 여부 필터링
        if(eventOption){
            sql += "and s.event IS NOT NULL ";
        }

        // TODO 3. dueDateOption에 따라 쿠폰사용 임박 여부 필터링
        if(dueDateOption){
            sql += "and st.dueDate=true ";
        }

        sql += "order by ST_DISTANCE_SPHERE(POINT(:userLongitude, :userLatitude), POINT(s.longitude, s.latitude))";

        return jdbcTemplate.query(sql, params, (rs, rowNum) ->
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
                        // event 값이 null이면 null, 아니면 Stores table의 event값 return
                        rs.getString("event") != null ? rs.getString("event") : null
                )
        );
    }
}

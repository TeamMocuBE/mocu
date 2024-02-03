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

    public List<GetMapStoreInfoResponse> getMapStoreInfoList(long userId, double latitude, double longitude, int distance) {
        // ST_DISTANCE_SPHERE 함수 이용
        String sql = "select s.name as storeName, s.mainImageUrl, s.category, st.dueDate, s.rating, " +
                "st.numOfStamp, s.maxStamp, st.numOfCouponAvailable, s.reward, " +
                "case when s.event is not null then true else false end as event " +
                "from Stores s join Stamps st on s.storeId=st.storeId " +
                "where ST_DISTANCE_SPHERE(POINT(:userLongitude, :userLatitude), POINT(s.longitude, s.latitude)) <= :distance " +
                "order by ST_DISTANCE_SPHERE(POINT(:userLongitude, :userLatitude), POINT(s.longitude, s.latitude))";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("userLongitude", longitude);
        params.addValue("userLatitude", latitude);
        params.addValue("distance", distance);

        return jdbcTemplate.query(sql, params, (rs, rowNum) ->
                new GetMapStoreInfoResponse(
                        rs.getString("storeName"),
                        rs.getString("mainImageUrl"),
                        rs.getString("category"),
                        rs.getBoolean("dueDate"),
                        rs.getFloat("rating"),
                        rs.getInt("numOfStamp"),
                        rs.getInt("maxStamp"),
                        rs.getInt("numOfCouponAvailable"),
                        rs.getString("reward"),
                        rs.getBoolean("event")
                )
        );
    }
}

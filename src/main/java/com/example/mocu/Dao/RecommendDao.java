package com.example.mocu.Dao;

import com.example.mocu.Dto.store.RecommendStoreInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.Collections;
import java.util.List;

@Slf4j
@Repository
public class RecommendDao {
    private final NamedParameterJdbcTemplate jdbcTemplate;

    public RecommendDao(DataSource dataSource){
        this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }


    public List<RecommendStoreInfo> getRecommendStoreInfoListForNewUser(int recommendLimit) {
        String sql = "select name as storeName, IFNULL(event, false) as event, mainImageUrl " +
                "from Stores order by rating desc limit :recommendLimit";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("recommendLimit", recommendLimit);

        List<RecommendStoreInfo> recommendStoreInfoList = jdbcTemplate.query(sql, params, (rs, rowNum) -> {
            RecommendStoreInfo recommendStoreInfo = new RecommendStoreInfo();
            recommendStoreInfo.setStoreName(rs.getString("storeName"));
            recommendStoreInfo.setEvent(rs.getBoolean("event"));
            recommendStoreInfo.setMainImageUrl(rs.getString("mainImageUrl"));
            return recommendStoreInfo;
        });

        // Return an empty list if the result is empty
        return recommendStoreInfoList.isEmpty() ? Collections.singletonList(new RecommendStoreInfo()) : recommendStoreInfoList;
    }

    public List<String> getFavoriteCategories(long userId, int numOfCategory) {
        String sql = "select distinct s.category from Stores s join Stamps st on s.storeId=st.storeId " +
                "where st.userId=:userId and st.status='active' order by max(st.modifiedDate) desc limit :numOfCategory";

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("userId", userId);
        params.addValue("numOfCategory", numOfCategory);

        return jdbcTemplate.queryForList(sql, params, String.class);
    }

    public RecommendStoreInfo getRecommendStoreInfo(String category, long userId, int recommendLimit) {
        String sql = "select s.name as storeName, IFNULL(event, false) as event, s.mainImageUrl " +
                "from Stores s where s.category=:category and s.storeId not in " +
                "(select st.storeId from Stamps st where st.userId=:userId) " +
                "order by s.rating desc limit :recommendLimit";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("category", category);
        params.addValue("userId", userId);
        params.addValue("recommendLimit", recommendLimit);

        return jdbcTemplate.queryForObject(sql, params, (rs, rowNum) ->
                new RecommendStoreInfo(
                        rs.getString("storeName"),
                        rs.getBoolean("event"),
                        rs.getString("mainImageUrl")
                )
        );
    }
}

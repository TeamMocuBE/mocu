package com.example.mocu.Dao;

import com.example.mocu.Dto.review.ReviewForUser;
import com.example.mocu.Dto.stamp.UserStampInfo;
import com.example.mocu.Dto.store.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Repository
@Slf4j
public class StoreDao {
    private final NamedParameterJdbcTemplate jdbcTemplate;

    public StoreDao(DataSource dataSource){
        this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }


    public List<String> getStoreImages(long storeId) {
        String sql = "select imageUrl from StoreImage where storeId=:storeId and status='active'";
        Map<String, Object> param = Map.of("storeId", storeId);
        return jdbcTemplate.queryForList(sql, param, String.class);
    }


    // 최신순 정렬
    public List<GetStoreReviewsResponse> getStoreReviewsOrderByTime(long storeId) {
        String sql = "select rate, content from Reviews where storeId=:storeId and status='작성이후' order by modifiedDate DESC";
        Map<String, Object> param = Map.of("storeId", storeId);
        return jdbcTemplate.query(sql, param,
                (rs, rowNum) -> new GetStoreReviewsResponse(
                        rs.getInt("rate"),
                        rs.getString("content")
                ));
    }

    // 평점높은순 정렬
    public List<GetStoreReviewsResponse> getStoreReviewsOrderByRate(long storeId) {
        String sql = "select rate, content from Reviews where storeId=:storeId and status='작성이후' order by rate DESC";
        Map<String, Object> param = Map.of("storeId", storeId);
        return jdbcTemplate.query(sql, param,
                (rs, rowNum) -> new GetStoreReviewsResponse(
                        rs.getInt("rate"),
                        rs.getString("content")
                ));
    }

    public StoreInfo getStoreInfo(long storeId) {
        String sql = "select name as storeName, category, reward, maxStamp, rating from Stores where storeId=:storeId";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("storeId", storeId);
        return jdbcTemplate.queryForObject(sql, params, new BeanPropertyRowMapper<>(StoreInfo.class));
    }

    public UserStampInfo getUserStampInfo(long storeId, long userId) {
        String sql = "select numOfStamp, numOfCouponAvailable from Stamps where userId=:userId and storeId=:storeId";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("userId", userId);
        params.addValue("storeId", storeId);

        try {
            return jdbcTemplate.queryForObject(sql, params, new BeanPropertyRowMapper<>(UserStampInfo.class));
        } catch (EmptyResultDataAccessException e){
            // Return default UserStampInfo with numOfStamp and numOfCouponAvailable set to 0
            return new UserStampInfo();
        }
    }

    public List<ReviewForUser> getReviews(long storeId, long userId) {
        String sql = "select rate, content, modifiedDate from Reviews where userId=:userId and storeId=:storeId";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("userId", userId);
        params.addValue("storeId", storeId);
        try {
            return jdbcTemplate.query(sql, params, new BeanPropertyRowMapper<>(ReviewForUser.class));
        } catch (EmptyResultDataAccessException e){
            // Return an empty list
            return Collections.emptyList();
        }
    }
  
    public List<GetSearchedStoreResponse> getSearchedStore(long userId, String query, String sort, String category, String option) {
        String sql = "SELECT s.storeId, s.name, s.reward, s.coordinate, s.rating "
                + "FROM Stores s LEFT JOIN (SELECT storeId, COUNT(*) as reviewCount FROM Reviews GROUP BY storeId) as rv ON s.storeId = rv.storeId ";

        sql += "WHERE (s.name LIKE CONCAT('%', :query, '%') OR s.category LIKE CONCAT('%', :query, '%')) ";

        if (category != null && !category.isEmpty()) {
            sql += "AND s.category = :category ";
        }

        if (Objects.equals(option, "적립 중인 곳만")) {
            sql += "AND EXISTS (SELECT 1 FROM Stamps st WHERE st.storeId = s.storeId AND st.userId = :userId) ";
        } else if (Objects.equals(option, "안가본 곳만")) {
            sql += "AND NOT EXISTS (SELECT 1 FROM Stamps st WHERE st.storeId = s.storeId AND st.userId = :userId) ";
        } else if (Objects.equals(option, "쿠폰 사용 임박")) {
            sql += "AND EXISTS (SELECT 1 FROM Stamps st WHERE st.storeId = s.storeId AND st.dueDate = TRUE AND st.userId = :userId) ";
        } else if (Objects.equals(option, "이벤트 중")) {
            sql += "AND s.event IS NOT NULL ";
        }

        if (sort != null && !sort.isEmpty()) {
            sql += "order by ";
            switch (sort) {
                case "별점 높은 순" -> {
                    sql += "s.rating DESC";
                    break;
                }
                case "리뷰 많은 순" -> {
                    sql += "rv.reviewCount DESC, ";
                    break;
                }
            }
        }

        // 마지막에 rv.reviewCount를 제거하여 원래 필요한 컬럼들만 선택
        sql = "SELECT s.storeId, s.name, s.reward, s.coordinate, s.rating FROM (" + sql + ") as s";

        Map<String, Object> param = Map.of(
                "userId", "%" + userId + "%",
                "query", "%" + query + "%",
                "category", "%" + category + "%"
        );

        return jdbcTemplate.query(sql, param,
                (rs, rowNum) -> new GetSearchedStoreResponse(
                        rs.getString("name"),
                        rs.getString("reward"),
                        rs.getString("coordinate"),
                        rs.getBigDecimal("rating"),
                        rs.getString("numOfStamp")
                )
        );
    }
}

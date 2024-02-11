package com.example.mocu.Dao;

import com.example.mocu.Dto.review.ReviewForUser;
import com.example.mocu.Dto.stamp.UserStampInfo;
import com.example.mocu.Dto.store.GetSearchedStoreResponse;
import com.example.mocu.Dto.store.GetStoreReviewsResponse;
import com.example.mocu.Dto.store.StoreInEventInfo;
import com.example.mocu.Dto.store.StoreInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

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

  
    public List<StoreInEventInfo> getStoreInEventInfoList(int limit) {
        String sql = "select name as storeName, mainImageUrl from Stores where event is not null " +
                "order by rand() limit :limit";
        // 일단은 랜덤하게 고르는 로직 사용

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("limit", limit);

        List<StoreInEventInfo> storeInEventInfoList = jdbcTemplate.query(sql, params, (rs, rowNul) -> {
            StoreInEventInfo storeInEventInfo = new StoreInEventInfo();
            storeInEventInfo.setStoreName(rs.getString("storeName"));
            storeInEventInfo.setMainImageUrl(rs.getString("mainImageUrl"));
            return storeInEventInfo;
        });

        // return null if the list is empty
        return storeInEventInfoList.isEmpty() ? null : storeInEventInfoList;
    }

    public List<GetSearchedStoreResponse> getSearchedStore(long userId, String query, String sort, String category, boolean savingOnly, boolean notVisitedOnly, boolean couponImminent, boolean eventOnly, double userLatitude, double userLongitude, int page) {
        int limit = 10;
        int offset = page * limit;

        String sql = "SELECT s.name, s.reward, s.latitude, s.longitude, s.rating, s.maxStamp, ST_Distance_Sphere(point(s.longitude, s.latitude), point(:userLongitude, :userLatitude)) AS distance "
                + "COALESCE(st.numOfStamp, 0) AS numOfStamp "
                + "FROM Stores s "
                + "LEFT JOIN (SELECT storeId, userId, numOfStamp FROM Stamps WHERE userId = :userId) st ON s.storeId = st.storeId "
                + "LEFT JOIN (SELECT storeId, COUNT(*) as reviewCount FROM Reviews GROUP BY storeId) rv ON s.storeId = rv.storeId ";

        sql += "WHERE ";

        if (query != null && !query.isEmpty()) {
            sql += "(s.name LIKE CONCAT('%', :query, '%') OR s.category LIKE CONCAT('%', :query, '%')) ";
        }

        if (category != null && !category.isEmpty()) {
            sql += "AND s.category = :category ";
        }

        List<String> conditions = new ArrayList<>();
        if (savingOnly) {
            conditions.add("EXISTS (SELECT 1 FROM Stamps st WHERE st.storeId = s.storeId AND st.userId = :userId)");
        }
        if (notVisitedOnly) {
            conditions.add("NOT EXISTS (SELECT 1 FROM Stamps st WHERE st.storeId = s.storeId AND st.userId = :userId)");
        }
        if (couponImminent) {
            conditions.add("EXISTS (SELECT 1 FROM Stamps st WHERE st.storeId = s.storeId AND st.dueDate = TRUE AND st.userId = :userId)");
        }
        if (eventOnly) {
            conditions.add("s.event IS NOT NULL");
        }

        if (!conditions.isEmpty()) {
            sql += "WHERE " + String.join(" AND ", conditions);
        }

        if (sort != null && !sort.isEmpty()) {
            sql += " order by ";
            switch (sort) {
                case "별점 높은 순" -> {
                    sql += "s.rating DESC";
                    break;
                }
                case "리뷰 많은 순" -> {
                    sql += "rv.reviewCount DESC";
                    break;
                }
                case "거리순" -> {
                    sql += "distance";
                    break;
                }
            }
        }

        sql += " limit :limit offset :offset";

        Map<String, Object> param = Map.of(
                "userId", userId,
                "query", query != null ? "%" + query + "%" : "%",
                "category", category != null ? category : "%",
                "userLatitude", userLatitude,
                "userLongitude", userLongitude,
                "limit", limit,
                "offset", offset
        );

        return jdbcTemplate.query(sql, param,
                (rs, rowNum) -> new GetSearchedStoreResponse(
                        rs.getString("name"),
                        rs.getString("reward"),
                        rs.getDouble("distance"),
                        rs.getBigDecimal("rating"),
                        rs.getInt("maxStamp"),
                        rs.getInt("numOfStamp")
                )
        );
    }


    public boolean isStoreInEvent(long storeId) {
        String sql = "select event from Stores where storeId=:storeId";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("storeId", storeId);

        String event = jdbcTemplate.queryForObject(sql, params, String.class);

        return event != null;
    }

    public boolean isDueDateStore(long userId, long storeId) {
        String sql = "select dueDate from Stamps where userId=:userId and storeId=:storeId";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("userId", userId);
        params.addValue("storeId", storeId);

        Boolean dueDate = jdbcTemplate.queryForObject(sql, params, Boolean.class);

        return dueDate;
    }

    public List<ReviewForUser> getReviewsOrderByTime(long storeId, int page) {
        int limit = 10;
        int offset = page * limit;

        // 리뷰 '최신순' 정렬
        String sql = "select u.name, u.userImage, r.rate, r.content, r.modifiedDate " +
                "from Users u join Reviews r on u.userId=r.userId " +
                "where r.storeId=:storeId and r.status='작성이후' " +
                "order by r.modifiedDate desc limit :limit offset :offset";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("storeId", storeId);
        params.addValue("limit", limit);
        params.addValue("offset", offset);

        List<ReviewForUser> reviews = jdbcTemplate.query(sql, params, (rs, rowNum) -> new ReviewForUser(
                rs.getString("name"),
                rs.getString("userImage"),
                rs.getInt("rate"),
                rs.getString("content"),
                timestampToString(rs.getTimestamp("modifiedDate"))
                )
        );

        // 가게에 등록된 리뷰가 없는 경우 빈 리스트 반환
        if(reviews.isEmpty()){
            return Collections.emptyList();
        }

        return reviews;
    }

    public List<ReviewForUser> getReviewsOrderByRate(long storeId, int page) {
        int limit = 10;
        int offset = page * limit;

        // 리뷰 '평점순' 정렬
        String sql = "select u.name, u.userImage, r.rate, r.content, r.modifiedDate " +
                "from Users u join Reviews r on u.userId=r.userId " +
                "where r.storeId=:storeId and r.status='작성이후' " +
                "order by r.rate desc limit :limit offset :offset";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("storeId", storeId);
        params.addValue("limit", limit);
        params.addValue("offset", offset);

        List<ReviewForUser> reviews = jdbcTemplate.query(sql, params, (rs, rowNum) -> new ReviewForUser(
                rs.getString("name"),
                rs.getString("userImage"),
                rs.getInt("rate"),
                rs.getString("content"),
                timestampToString(rs.getTimestamp("modifiedDate"))
                )
        );

        // 가게에 등록된 리뷰가 없는 경우 빈 리스트 반환
        if(reviews.isEmpty()){
            return Collections.emptyList();
        }

        return reviews;
    }

    private String timestampToString(Timestamp timestamp) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        return (timestamp != null) ? dateFormat.format(timestamp) : null;
    }


    public void updateStoreRating(long storeId, int rate) {
        // TODO 1. 해당 store에 등록된 review 개수 count
        String countSql = "select count(*) from Reviews where storeId=:storeId and status='작성이후'";
        MapSqlParameterSource countParams = new MapSqlParameterSource();
        countParams.addValue("storeId", storeId);
        int count = jdbcTemplate.queryForObject(countSql, countParams, Integer.class);

        // TODO 2. 해당 store에 등록된 rate의 sum 계산
        String totalRateSql = "select sum(rate) from Reviews where storeId=:storeId and status='작성이후'";
        MapSqlParameterSource totalRateParams = new MapSqlParameterSource();
        totalRateParams.addValue("storeId", storeId);
        int totalRate = jdbcTemplate.queryForObject(totalRateSql, totalRateParams, Integer.class);

        // TODO 3. 해당 store의 rating 값 update
        // newRating double로 typeCasting
        double newRating = ((double)(totalRate + rate)) / (count + 1);
        // 소수점 이하 1자리로 반올림
        DecimalFormat df = new DecimalFormat("#.#");
        BigDecimal roundedRating = new BigDecimal(df.format(newRating));

        String updateSql = "update Stores set rating=:rating where storeId=:storeId";
        MapSqlParameterSource updateParams = new MapSqlParameterSource();
        updateParams.addValue("rating", roundedRating);
        updateParams.addValue("storeId", storeId);

        jdbcTemplate.update(updateSql, updateParams);
    }
}

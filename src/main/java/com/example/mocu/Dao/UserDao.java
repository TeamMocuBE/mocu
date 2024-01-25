package com.example.mocu.Dao;

import com.example.mocu.Dto.user.GetMyPageResponse;
import com.example.mocu.Dto.user.GetUserResponse;
import com.example.mocu.Dto.user.PostUserRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Repository
public class UserDao {
    private final NamedParameterJdbcTemplate jdbcTemplate;

    public UserDao(DataSource dataSource) {
        this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

    public List<GetUserResponse> getUsers(String name, String email, String status) {
        String sql = "select userId, name, email, userImage, status, oAuthProvider from Users " +
                "where name like :name and email like :email and status like :status";

        Map<String, Object> param =Map.of(
                "name", "%" + name + "%",
                "email", "%" + email + "%",
                "status", status);

        return jdbcTemplate.query(sql, param,
                (rs, rowNum) -> new GetUserResponse(
                        rs.getLong("userId"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("userImage"),
                        rs.getString("status"),
                        rs.getString("oAuthProvider")
                )
        );
    }

    public long getUserIdByEmail(String email) {
        String sql = "select userId from Users where email=:email and status='active'";
        Map<String, Object> param = Map.of("email", email);
        return jdbcTemplate.queryForObject(sql, param, long.class);
    }

    public long createUser(PostUserRequest postUserRequest) {
        String sql = "insert into user(email, name, provider, profile_image) " +
                "values(:email, :name, :provider, :profileImage)";

        SqlParameterSource param = new BeanPropertySqlParameterSource(postUserRequest);
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(sql, param, keyHolder);

        return Objects.requireNonNull(keyHolder.getKey()).longValue();
    }


    public GetMyPageResponse getMypage(Long userId) {
        String sqlUsableCoupon = "SELECT SUM(numOfCouponAvailable) FROM Stamps " +
                "WHERE userId = :userId AND status = 'active'";
        String sqlAvailableFavoriteCount = "SELECT S.name " +
                "FROM Regulars R " +
                "JOIN Stores S ON R.storeId = S.storeId " +
                "WHERE R.status = 'request' AND R.userId = :userId";
        String sqlCurrentAddress = "select address from Addresses " +
                "where status like 'select'";
        String sqlRecentCouponUsage = "SELECT S.name AS storeName, S.reward AS benefit " +
                "FROM CouponsRequest CR " +
                "JOIN Stores S ON CR.storeId = S.storeId " +
                "WHERE CR.userId = :userId AND CR.status = 'accepted' " +
                "AND CR.createdDate >= CURDATE() - INTERVAL 1 MONTH " +
                "ORDER BY CR.createdDate DESC " +
                "limit 5";
        String sqlAvailableReviewCount = "select COUNT(*) from reviews where userId = :userId and status = '작성 이전'";
        String sqlMissionStampCount = "select numOfStamp from MissionStamp where userId = :userId";

        Map<String, Object> param = Map.of("userId", userId);

        // 사용 가능한 쿠폰 개수 조회
        Integer usableCoupon = jdbcTemplate.queryForObject(sqlUsableCoupon, param, Integer.class);

        // 단골 설정 가능한 가게 수 조회 (여기서는 가게 이름 목록을 가져옴)
        List<String> availableFavoriteStoreNames = jdbcTemplate.query(sqlAvailableFavoriteCount, param,
                (rs, rowNum) -> rs.getString("name"));
        int availableFavoriteCount = availableFavoriteStoreNames.size();

        // 현재 선택된 주소 조회
        String currentAddress = jdbcTemplate.queryForObject(sqlCurrentAddress, Collections.emptyMap(), String.class);

        // 최근 쿠폰 사용 내역 조회
        List<GetMyPageResponse.CouponUsageDetail> recentCouponUsage = jdbcTemplate.query(sqlRecentCouponUsage, param,
                (rs, rowNum) -> new GetMyPageResponse.CouponUsageDetail(
                        rs.getString("benefit"),
                        rs.getString("storeName")
                ));

        // 작성 가능한 리뷰 개수 조회
        Integer availableReviewCount = jdbcTemplate.queryForObject(sqlAvailableReviewCount, param, Integer.class);

        // 미션 스탬프 개수 조회
        Integer missionStampCount = jdbcTemplate.queryForObject(sqlMissionStampCount, param, Integer.class);

        return new GetMyPageResponse(
                usableCoupon != null ? usableCoupon : 0,
                availableFavoriteCount,
                currentAddress,
                recentCouponUsage,
                availableReviewCount != null ? availableReviewCount : 0,
                missionStampCount != null ? missionStampCount : 0
        );

    public List<Long> getAllUserIds() {
        String sql = "select userId from Users";

        return jdbcTemplate.queryForList(sql, new MapSqlParameterSource(), Long.class);
    }
}
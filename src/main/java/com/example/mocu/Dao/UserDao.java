package com.example.mocu.Dao;

import com.example.mocu.Dto.search.Search;
import com.example.mocu.Dto.store.DueDateStoreInfo;
import com.example.mocu.Dto.store.RecentlyVisitedStoreInfo;
import com.example.mocu.Dto.user.*;
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
        String sql = "select userId, name, email, userImage, status, provider from Users " +
                "where name like :name and email like :email and status like :status";

        Map<String, Object> param = Map.of(
                "name", "%" + name + "%",
                "email", "%" + email + "%",
                "status", "%" + status + "%");

        return jdbcTemplate.query(sql, param,
                (rs, rowNum) -> new GetUserResponse(
                        rs.getLong("userId"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("userImage"),
                        rs.getString("status"),
                        rs.getString("provider")
                )
        );
    }

    public long getUserIdByEmail(String email) {
        String sql = "select userId from Users where email=:email and status='active'";
        Map<String, Object> param = Map.of("email", email);
        return jdbcTemplate.queryForObject(sql, param, long.class);
    }

    public long createUser(PostUserRequest postUserRequest) {
        String sql = "insert into users(email, name, provider, userImage) " +
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
    }

    public List<Long> getAllUserIds() {
        String sql = "select userId from Users";

        return jdbcTemplate.queryForList(sql, new MapSqlParameterSource(), Long.class);
    }

    public List<Search> getRecentSearchesForUser(long userId, int limit) {
        String sql = "select query from RecentSearch where userId=:userId order by createdDate desc limit :limit";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("userId", userId);
        params.addValue("limit", limit);

        List<Search> recentSearches = jdbcTemplate.query(sql, params, (rs, rowNum) -> {
            Search search = new Search();
            search.setContent(rs.getString("query"));
            return search;
        });

        // return null if the list is empty
        return recentSearches.isEmpty() ? null : recentSearches;
    }

    public List<RecentlyVisitedStoreInfo> getRecentlyVisitedStoreInfoListForUser(long userId, int limit) {
        String sql = "select s.name storeName, st.numOfCouponAvailable, IFNULL(s.event, false) as event, " +
                "s.coordinate from Stores s join Stamps st on s.storeId=st.storeId where st.userId=:userId and st.status='active' " +
                "group by s.storeId order by MAX(st.modifiedDate) desc limin :limit";
        // IFNULL 함수 : Stores table의 event 값이 null이면 false를 반환
        // Stamps table의 tuple이 수정 <-> 해당 user가 해당 가게에 방문했다는 의미

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("userId", userId);
        params.addValue("limit", limit);

        List<RecentlyVisitedStoreInfo> recentlyVisitedStoreInfoList = jdbcTemplate.query(sql, params, (rs, rowNum) -> {
            RecentlyVisitedStoreInfo storeInfo = new RecentlyVisitedStoreInfo();
            storeInfo.setStoreName(rs.getString("storeName"));
            storeInfo.setNumOfCouponAvailable(rs.getInt("numOfCouponAvailable"));
            storeInfo.setEvent(rs.getBoolean("event"));
            storeInfo.setCoordinate(rs.getString("coordinate"));
            return storeInfo;
        });

        // return null if the list is empty
        return recentlyVisitedStoreInfoList.isEmpty() ? null : recentlyVisitedStoreInfoList;
    }

    public List<DueDateStoreInfo> getDueDateStoreInfoListForUser(long userId, int limit) {
        String sql = "select st.numOfStamp, s.maxStamp, s.name as storeName, s.coordinate from " +
                "Stamps st join Stores s on st.storeId=s.storeId where st.userId=:userId and st.dueDate=true and st.status='active' " +
                "order by st.modifiedDate desc limit :limit";
        // 수정일을 기준으로 limit 개수만큼 제한

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("userId", userId);
        params.addValue("limit", limit);

        List<DueDateStoreInfo> dueDateStoreInfoList = jdbcTemplate.query(sql, params, (rs, rowNum) -> {
            DueDateStoreInfo dueDateStoreInfo = new DueDateStoreInfo();
            dueDateStoreInfo.setNumOfStamp(rs.getInt("numOfStamp"));
            dueDateStoreInfo.setMaxStamp(rs.getInt("maxStamp"));
            dueDateStoreInfo.setStoreName(rs.getString("storeName"));
            dueDateStoreInfo.setCoordinate(rs.getString("coordinate"));
            return dueDateStoreInfo;
        });

        // return null if the list is empty
        return dueDateStoreInfoList.isEmpty() ? null : dueDateStoreInfoList;
    }

    public void updateRegularStatus(PatchUserRegularRequest patchUserRegularRequest) {
        String sql = "update Regulars set status=:status where userId=:userId and storeId=:storeId";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("status", patchUserRegularRequest.isRequest() ? "accept" : "request");
        params.addValue("userId", patchUserRegularRequest.getUserId());
        params.addValue("storeId", patchUserRegularRequest.getStoreId());

        jdbcTemplate.update(sql, params);
    }

    
    public boolean isRegular(long userId, long storeId) {
        String sql = "select count(*) from Regulars where userId=:userId and storeId=:storeId and status='accept'";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("userId", userId);
        params.addValue("storeId", storeId);
        
        Integer count = jdbcTemplate.queryForObject(sql, params, Integer.class);
        return count != null && count > 0;
    }

    /**
     * 단골 페이지에 나올 가게 리스트
     */
    public List<GetRegularResponse> getMyStoreList(long userId, String category, String sort, boolean isEventTrue, boolean isCouponUsable, double userLatitude, double userLongitude, int page) {
        int limit = 10;
        int offset = page * limit;

        String sql = "select s.mainImageUrl, s.name, st.numOfStamp, s.maxStamp, s.reward, s.coordinate, s.event ";
        sql += "ST_Distance_Sphere(point(s.longitude, s.latitude), point(:userLongitude, :userLatitude)) AS distance ";
        sql += "from stores s ";
        sql += "join stamps st on s.storeId = st.storeId and st.userId = :userId ";
        sql += "join regulars r on s.storeId = r.storeId and r.userId = :userId ";
        sql += "where s.status = 'active' and r.status = 'accept' ";

        // 이벤트가 있는 상점만 필터링
        if (isEventTrue) {
            sql += "AND s.event IS NOT NULL ";
        }

        // 사용 가능한 쿠폰이 있는 상점만 필터링
        if (isCouponUsable) {
            sql += "AND st.numOfCouponAvailable > 0 ";
        }

        if (category != null && !category.isEmpty()) {
            sql += "AND s.category = :category ";
        }

        if (sort != null && !sort.isEmpty()) {
            sql += "order by ";
            switch (sort) {
                case "최신순" -> {
                    sql += "st.modifiedDate DESC";
                    break;
                }
                case "적립 많은 순" -> {
                    sql += "st.numOfStamp";
                    break;
                }
                case "별점 높은 순" -> {
                    sql += "s.rate";
                    break;
                }
                case "거리순" -> {
                    sql += "distance";
                    break;
                }
                //TODO: 정렬 조건 추가하기
            }
        }

        sql += " LIMIT :limit OFFSET :offset";

        assert sort != null;
        Map<String, Object> param = Map.of(
                "userId", userId,
                "category", category != null ? category : "",
                "sort", sort,
                "userLatitude", userLatitude,
                "userLongitude", userLongitude,
                "limit", limit,
                "offset", offset
        );

        return jdbcTemplate.query(sql, param,
                (rs, rowNum) -> new GetRegularResponse(
                        rs.getString("mainImageUrl"),
                        rs.getString("name"),
                        rs.getInt("numOfStamp"),
                        rs.getInt("maxStamp"),
                        rs.getString("reward"),
                        rs.getString("coordinate")
                ));
    }

    public int getRegularsCount(long userId) {
        String sql = "SELECT COUNT(*) FROM Regulars WHERE userId = :userId AND status = 'request'";
        Map<String, Object> param = Map.of("userId", "%" + userId + "%");
        return jdbcTemplate.queryForObject(sql, param, Integer.class);
    }

    public void createRegularId(long userId, long storeId) {
        String sql = "insert into Regulars (userId, storeId) values (:userId, :storeId)";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("userId", userId);
        params.addValue("storeId", storeId);

        jdbcTemplate.update(sql, params);
    }


    public boolean isExistRegularId(long userId, long storeId) {
        String sql = "select count(*) from Regulars where userId=:userId and storeId=:storeId";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("userId", userId);
        params.addValue("storeId", storeId);

        int count = jdbcTemplate.queryForObject(sql, params, Integer.class);

        return count > 0;
    }

    public String getRegularStatus(long userId, long storeId) {
        String sql = "select status from Regulars where userId=:userId and storeId=:storeId";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("userId", userId);
        params.addValue("storeId", storeId);

        return jdbcTemplate.queryForObject(sql, params, String.class);
    }

    public long getRegularId(long userId, long storeId) {
        String sql = "select regularId from Regulars where userId=:userId and storeId=:storeId";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("userId", userId);
        params.addValue("storeId", storeId);

        return jdbcTemplate.queryForObject(sql, params, long.class);
    }
  
  
    public List<GetStoreCanBeRegularResponse> getStoreCanBeRegularList(long userId, double userLatitude, double userLongitude, int page) {
        int limit = 5;
        int offset = limit * page;

        // Regulars table에서 status = 'request' -> 단골로 설정가능 BUT 아직 단골은 X
        String sql = "select s.storeId, s.mainImageUrl, s.name as storeName, st.numOfStamp, s.maxStamp, s.reward, s.rating, " +
                "ST_DISTANCE_SPHERE(POINT(s.longitude, s.latitude), POINT(:userLongitude, :userLatitude)) as distance " +
                "from Stores s " +
                "join Stamps st on s.storeId=st.storeId and st.userId=:userId " +
                "join Regulars r on s.storeId=r.storeId and r.userId=:userId " +
                "where s.status='active' and r.status='request' " +
                "order by distance limit :limit offset :offset";

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("userLongitude", userLongitude);
        params.addValue("userLatitude", userLatitude);
        params.addValue("userId", userId);
        params.addValue("limit", limit);
        params.addValue("offset", offset);

        return jdbcTemplate.query(sql, params, (rs, rowNum) -> new GetStoreCanBeRegularResponse(
                rs.getLong("storeId"),
                rs.getString("mainImageUrl"),
                rs.getString("storeName"),
                rs.getInt("numOfStamp"),
                rs.getInt("maxStamp"),
                rs.getString("reward"),
                rs.getFloat("rating"),
                rs.getDouble("distance")
            )
        );
    }

    public int updateRegularStatusToNotAccept(PatchUserRegularRequest patchUserRegularRequest) {
        String sql = "update Regulars set status='not-accept' where userId=:userId and storeId=:storeId";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("userId", patchUserRegularRequest.getUserId());
        params.addValue("storeId", patchUserRegularRequest.getStoreId());

        return jdbcTemplate.update(sql, params);
    }
  
    public int getUserIdCount(Long userId) {
        String sql = "select Count(*) as userIdCount from Users where userId = :userId";
        Map<String, Object> param = Map.of("userId", userId);

        return jdbcTemplate.queryForObject(sql, param, Integer.class);
    }
}
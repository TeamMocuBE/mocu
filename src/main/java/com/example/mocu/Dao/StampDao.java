package com.example.mocu.Dao;

import com.example.mocu.Dto.stamp.GetStampStoreAroundResponse;
import com.example.mocu.Dto.stamp.PostStampRequest;
import com.example.mocu.Dto.stamp.PostStampResponse;
import com.example.mocu.Dto.stamp.StampInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Objects;

@Slf4j
@Repository
public class StampDao {
    private final NamedParameterJdbcTemplate jdbcTemplate;

    public StampDao(DataSource dataSource){
        this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

    public PostStampResponse stampRequestRegister(PostStampRequest postStampRequest) {
        String sql = "insert into StampsRequest(userId, storeId) values(:userId, :storeId)";

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("userId", postStampRequest.getUserId());
        params.addValue("storeId", postStampRequest.getStoreId());

        jdbcTemplate.update(sql, params);

        String selectSql = "select sr.stampRequestId, sr.createdDate, " +
                "(select s.address from Stores s where s.storeId=:storeId) as storeAddress, " +
                "(select u.name from Users u where u.userId=:userId) as userName " +
                "from StampsRequest sr where sr.userId=:userId and sr.storeId=:storeId";

        MapSqlParameterSource selectParams = new MapSqlParameterSource();
        selectParams.addValue("userId", postStampRequest.getUserId());
        selectParams.addValue("storeId", postStampRequest.getStoreId());

        return jdbcTemplate.queryForObject(selectSql, selectParams, (rs, rowNum) ->
                new PostStampResponse(
                        rs.getLong("stampRequestId"),
                        postStampRequest.getUserId(),
                        postStampRequest.getStoreId(),
                        timestampToString(rs.getTimestamp("createdDate")),
                        rs.getString("storeAddress"),
                        rs.getString("userName")
                )
        );
    }

    private String timestampToString(Timestamp timestamp) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        return (timestamp != null) ? dateFormat.format(timestamp) : null;
    }

    public void updateStampsRequestStatusToAccept(long stampRequestId) {
        log.info("[StampDao.updateStampsRequestStatusToAccept]");

        String sql = "update StampsRequest set status='accept' where stampRequestId=:stampRequestId";

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("stampRequestId", stampRequestId);
        jdbcTemplate.update(sql, params);
    }

    public boolean doesStampsEntityExist(long userId, long storeId) {
        log.info("[StampDao.doesStampsEntityExist]");

        String sql = "select count(*) from Stamps where userId=:userId and storeId=:storeId";
        
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("userId", userId);
        params.addValue("storeId", storeId);
        
        int rowCount = jdbcTemplate.queryForObject(sql, params, Integer.class);
        return rowCount > 0;
    }

    public StampInfo updateNumOfStamp(long userId, long storeId, int numOfStamp) {
        log.info("[StampDao.updateNumOfStamp]");

        // 1. update
        String sql = "update Stamps set numOfStamp=numOfStamp+:numOfStamp " +
                "where userId=:userId and storeId=:storeId";

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("userId", userId);
        params.addValue("storeId", storeId);
        params.addValue("numOfStamp", numOfStamp);

        jdbcTemplate.update(sql, params);

        // 2. select
        String selectSql = "select stampId, numOfStamp from Stamps where userId=:userId and storeId=:storeId";
        MapSqlParameterSource selectParams = new MapSqlParameterSource();
        selectParams.addValue("userId", userId);
        selectParams.addValue("storeId", storeId);

        return jdbcTemplate.queryForObject(selectSql, params, (rs, rowNum) ->
                new StampInfo(
                        rs.getLong("stampId"),
                        rs.getInt("numOfStamp")
                )
        );
    }


    public StampInfo createNewStampsEntry(long userId, long storeId, int numOfStamp) {
        log.info("[StampDao.createNewStampsEntry]");

        // 1. insert
        String sql = "insert into Stamps (userId, storeId, numOfStamp, modifiedDate) values " +
                "(:userId, :storeId, :numOfStamp, now())";

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("userId", userId);
        params.addValue("storeId", storeId);
        params.addValue("numOfStamp", numOfStamp);

        jdbcTemplate.update(sql, params);

        // 2. select
        String selectSql = "select stampId, numOfStamp from Stamps where userId=:userId and storeId=:storeId";
        MapSqlParameterSource selectParams = new MapSqlParameterSource();
        selectParams.addValue("userId", userId);
        selectParams.addValue("storeId", storeId);

        return jdbcTemplate.queryForObject(selectSql, params, (rs, rowNum) ->
                new StampInfo(
                        rs.getLong("stampId"),
                        rs.getInt("numOfStamp")
                )
        );
    }


    public int getMaxStampValue(long storeId) {
        log.info("[StampDao.getMaxStampValue]");

        String sql = "select maxStamp from Stores where storeId=:storeId";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("storeId", storeId);

        return jdbcTemplate.queryForObject(sql, params, Integer.class);
    }

    public void updateDueDate(long stampId, boolean isDueDateTrue) {
        log.info("[StampDao.updateDueDate]");

        String sql = "update Stamps set dueDate=:isDueDateTrue where stampId=:stampId";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("isDueDateTrue", isDueDateTrue);
        params.addValue("stampId", stampId);
        jdbcTemplate.update(sql, params);
    }

    public int updateNumOfCouponAvailable(long stampId, int numOfCouponAvailable) {
        log.info("[StampDao.updateNumOfCouponAvailable]");

        String sql = "update Stamps set numOfCouponAvailable=:numOfCouponAvailable where stampId=:stampId";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("numOfCouponAvailable", numOfCouponAvailable);
        params.addValue("stampId", stampId);
        jdbcTemplate.update(sql, params);

        return numOfCouponAvailable;
    }

    public String getStoreName(long storeId) {
        String sql = "select name from Stores where storeId=:storeId";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("storeId", storeId);

        return jdbcTemplate.queryForObject(sql, params, String.class);
    }

    public List<GetStampStoreAroundResponse> getStampStoreAroundList(long userId, double latitude, double longitude, int page) {
        int limit = 5;
        int offset = limit * page;

        // 현재 user위치와 가까운 store 순으로 정렬해서 return
        String sql = "select s.mainImageUrl, s.name as storeName, st.numOfStamp, s.maxStamp, st.numOfCouponAvailable, s.reward, s.rating, " +
                "ST_DISTANCE_SPHERE(POINT(s.longitude, s.latitude), point(:userLongitude, :userLatitude)) as distance " +
                "from Stores s join Stamps st on s.storeId=st.storeId where st.userId=:userId and st.numOfStamp>0 " +
                "order by distance limit :limit offset :offset";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("userLongitude", longitude);
        params.addValue("userLatitude", latitude);
        params.addValue("userId", userId);
        params.addValue("limit", limit);
        params.addValue("offset", offset);

        return jdbcTemplate.query(sql, params, (rs, rowNum) -> new GetStampStoreAroundResponse(
                rs.getString("mainImageUrl"),
                rs.getString("storeName"),
                rs.getInt("numOfStamp"),
                rs.getInt("maxStamp"),
                rs.getInt("numOfCouponAvailable"),
                rs.getString("reward"),
                rs.getFloat("rating"),
                rs.getDouble("distance")
            )
        );
    }

    public List<Long> getStoreIdsStampedByUser(long userId, double latitude, double longitude) {
        log.info("[StampDao.getStoreIdsStampedByUser]");
        int limit = 5;

        String sql = "select s.storeId from Stamps st join Stores s on st.storeId=s.storeId " +
                "where st.userId=:userId and st.numOfStamp > 0 " +
                "order by ST_DISTANCE_SPHERE(POINT(s.longitude, s.latitude), point(:userLongitude, :userLatitude)) " +
                "limit :limit";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("userId", userId);
        params.addValue("userLongitude", longitude);
        params.addValue("userLatitude", latitude);
        params.addValue("limit", limit);

        List<Long> storeIds = jdbcTemplate.queryForList(sql, params, Long.class);

        return storeIds.isEmpty() ? null : storeIds;
    }
}

package com.example.mocu.Dao;

import com.example.mocu.Dto.coupon.PostCouponAcceptRequest;
import com.example.mocu.Dto.coupon.PostCouponRequest;
import com.example.mocu.Dto.coupon.PostCouponResponse;
import com.example.mocu.Dto.stamp.StampInfo;
import com.example.mocu.Dto.stamp.StampInfoAfterCouponUse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

@Slf4j
@Repository
public class CouponDao {
    private final NamedParameterJdbcTemplate jdbcTemplate;

    public CouponDao(DataSource dataSource){
        this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

    public PostCouponResponse couponRequestRegister(PostCouponRequest postCouponRequest) {
        String sql = "insert into CouponsRequest(userId, storeId) values(:userId, :storeId)";

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("userId", postCouponRequest.getUserId());
        params.addValue("storeId", postCouponRequest.getStoreId());

        jdbcTemplate.update(sql, params);

        String selectSql = "select cr.couponRequestId, cr.createdDate, " +
                "(select s.address from Stores s where s.storeId=:storeId) as storeAddress, " +
                "(select u.name from Users u where u.userId=:userId) as UserName " +
                "from CouponsRequest cr where cr.userId=:userId and cr.storeId=:storeId";

        MapSqlParameterSource selectParams = new MapSqlParameterSource();
        selectParams.addValue("userId", postCouponRequest.getUserId());
        selectParams.addValue("storeId", postCouponRequest.getStoreId());

        return jdbcTemplate.queryForObject(selectSql, selectParams, (rs, rowNum) ->
                new PostCouponResponse(
                        rs.getLong("couponRequestId"),
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

    public void updateCouponsRequestStatusToAccept(long couponRequestId) {
        String sql = "update CouponsRequest set status='accept' where couponRequestId=:couponRequestId";

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("couponRequestId", couponRequestId);
        jdbcTemplate.update(sql, params);
    }


    public StampInfoAfterCouponUse updateStampsTable(PostCouponAcceptRequest postCouponAcceptRequest, int maxStamp) {
        String sql = "update Stamps set numOfStamp=numOfStamp-:maxStamp, " +
                "numOfCouponAvailable=numOfCouponAvailable-1, " +
                "useCount=useCount+1 where userId=:userId and storeId=:storeId";

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("userId", postCouponAcceptRequest.getUserId());
        params.addValue("storeId", postCouponAcceptRequest.getStoreId());
        params.addValue("maxStamp", maxStamp);

        jdbcTemplate.update(sql, params);

        String selectSql = "select stampId, numOfStamp, numOfCouponAvailable from Stamps " +
                "where userId=:userId and storeId=:storeId";

        return jdbcTemplate.queryForObject(selectSql, params, (rs, rowNum) ->
                new StampInfoAfterCouponUse(
                        rs.getLong("stampId"),
                        rs.getInt("numOfStamp"),
                        rs.getInt("numOfCouponAvailable")
                )
        );
    }


    public String getStoreReward(long storeId) {
        String sql = "select reward from Stores where storeId=:storeId";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("storeId", storeId);
        return jdbcTemplate.queryForObject(sql, params, String.class);
    }
}

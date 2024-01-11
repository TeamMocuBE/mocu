package com.example.mocu.Dao;

import com.example.mocu.Dto.store.GetDetailedStoreResponse;
import com.example.mocu.Dto.store.GetNumberOfStampStoreResponse;
import com.example.mocu.Dto.store.GetStoreImagesResponse;
import com.example.mocu.Dto.store.GetStoreReviewsResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;
import java.util.Map;

@Repository
@Slf4j
public class StoreDao {
    private final NamedParameterJdbcTemplate jdbcTemplate;

    public StoreDao(DataSource dataSource){
        this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

    public GetDetailedStoreResponse getDetailedStore(long storeId) {
        String sql = "select category, name, phone, maxStamp, reward, rating from Stores where storeId=:storeId and status='active'";
        Map<String, Object> param = Map.of("storeId", storeId);
        return jdbcTemplate.queryForObject(sql, param,
                (rs, rowNum) -> new GetDetailedStoreResponse(
                        rs.getString("category"),
                        rs.getString("name"),
                        rs.getString("phone"),
                        rs.getInt("maxStamp"),
                        rs.getString("reward"),
                        rs.getFloat("rating")
                ));
    }

    public List<GetStoreImagesResponse> getStoreImages(long storeId) {
        String sql = "select imageUrl from StoreImage where storeId=:storeId and status='active'";
        Map<String, Object> param = Map.of("storeId", storeId);
        return jdbcTemplate.query(sql, param, (rs, rowNum) -> new GetStoreImagesResponse(rs.getString("imageUrl")));
    }

    public GetNumberOfStampStoreResponse getStoreStamps(long storeId, long userId) {
        String sql = "select numOfStamp from Coupons where storeId=:storeId and userId=:userId and status='active'";
        Map<String, Object> param = Map.of("storeId", storeId, "userId", userId);
        return jdbcTemplate.queryForObject(sql, param, (rs, rowNum) -> new GetNumberOfStampStoreResponse(rs.getInt("numOfStamp")));
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
}

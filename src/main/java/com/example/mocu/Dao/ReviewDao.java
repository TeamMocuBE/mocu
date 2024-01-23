package com.example.mocu.Dao;

import com.example.mocu.Dto.review.GetAvailableReviewCountResponse;
import com.example.mocu.Dto.review.PostReviewRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Objects;

@Slf4j
@Repository
public class ReviewDao {
    private final NamedParameterJdbcTemplate jdbcTemplate;

    public ReviewDao(NamedParameterJdbcTemplate jdbcTemplate){
        this.jdbcTemplate = jdbcTemplate;
    }

    public long createReview(PostReviewRequest postReviewReqeust) {
        String sql = "insert into Review(userId, storeId, rate, content) " + "values(:userId, :storeId, :rate, :content)";
        SqlParameterSource parameterSource = new BeanPropertySqlParameterSource(postReviewReqeust);
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(sql, parameterSource, keyHolder);
        return Objects.requireNonNull(keyHolder.getKey()).longValue();
    }


    public GetAvailableReviewCountResponse getAvailableReviewCount(Long userId) {
        String sql = "select COUNT(*) from reviews where userId = :userId and status = '작성 이전'";

        Map<String, Object> params = Map.of("userId", userId);

        int count = jdbcTemplate.queryForObject(sql, params, Integer.class);

        return new GetAvailableReviewCountResponse(count);
    }
}

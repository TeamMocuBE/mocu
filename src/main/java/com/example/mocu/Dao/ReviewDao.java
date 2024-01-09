package com.example.mocu.Dao;

import com.example.mocu.Dto.review.PostReviewRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

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

    /*
    public boolean isNotStamped(long storeId, long userId) {

    }

     */

    /*
    public boolean isNotSatisfiedReviewLength(String content) {

    }

     */


}

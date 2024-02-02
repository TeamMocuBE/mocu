package com.example.mocu.Dao;

import com.example.mocu.Dto.review.GetAvailableReviewResponse;
import com.example.mocu.Dto.review.PatchReviewReportToTrueRequest;
import com.example.mocu.Dto.review.PostReviewRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Repository
public class ReviewDao {
    private final NamedParameterJdbcTemplate jdbcTemplate;
    public ReviewDao(NamedParameterJdbcTemplate jdbcTemplate){
        this.jdbcTemplate = jdbcTemplate;
    }

    public long createReview(PostReviewRequest postReviewRequest) {
        String sql = "insert into Reviews(userId, storeId, rate, content) values (:userId, :storeId, :rate, :content)";
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("userId", postReviewRequest.getUserId())
                .addValue("storeId", postReviewRequest.getStoreId())
                .addValue("rate", postReviewRequest.getRate())
                .addValue("content", postReviewRequest.getContent());

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(sql, params, keyHolder);

        return Objects.requireNonNull(keyHolder.getKey()).longValue();
    }


    public List<GetAvailableReviewResponse> getAvailableReview(Long userId) {
        String sql = "select s.mainImageUrl, r.createdDate, s.name, s.category, s.maxStamp, st.numOfStamp, s.reward ";

        sql += "from Stores s ";

        sql += "join Reviews r on s.storeId = r.storeId ";

        sql += "join Stamps st on s.storeId = st.storeId and st.userId = :userId ";

        sql += "where r.status = '작성 이전' and s.status = 'active'";

        Map<String, Object> param = Map.of("userId", "%" + userId + "%");

        return jdbcTemplate.query(sql, param,
                (rs, rowNum) -> new GetAvailableReviewResponse(
                        rs.getString("mainImageUrl"),
                        rs.getTimestamp("createdDate").toString(),
                        rs.getString("name"),
                        rs.getString("category"),
                        rs.getInt("maxStamp"),
                        rs.getInt("numOfStamp"),
                        rs.getString("reward")
                )
        );
    }

    public void updateReviewReportToTrue(PatchReviewReportToTrueRequest patchReviewReportToTrueRequest) {
        String sql = "update Reviews set report=true where reviewId=:reviewId";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("reviewId", patchReviewReportToTrueRequest.getReviewId());

        jdbcTemplate.update(sql, params);
    }
}

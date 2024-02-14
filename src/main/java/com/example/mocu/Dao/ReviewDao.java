package com.example.mocu.Dao;

import com.example.mocu.Dto.review.*;
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

    public long createReviewId(long userId, long storeId, int rate, String content, String status) {
        String sql = "insert into Reviews(userId, storeId, rate, content, createdDate, modifiedDate, status) values (:userId, :storeId, :rate, :content, now(), now(), :status)";
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("userId", userId)
                .addValue("storeId", storeId)
                .addValue("rate", rate)
                .addValue("content", content)
                .addValue("status",status);

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(sql, params, keyHolder);

        return Objects.requireNonNull(keyHolder.getKey()).longValue();
    }

    public List<GetAvailableReviewResponse> getAvailableReview(Long userId) {
        String sql = "select s.mainImageUrl, r.createdDate, s.name, s.category, s.maxStamp, st.numOfStamp, s.reward ";

        sql += "from Stores s ";

        sql += "join Reviews r on s.storeId = r.storeId ";

        sql += "join Stamps st on s.storeId = st.storeId and st.userId = :userId ";

        sql += "where r.status = '작성이전' and s.status = 'active'";

        Map<String, Object> param = Map.of("userId", userId);

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

    public List<GetMyReviewResponse> getMyReview(Long userId, String sort) {
        String sql = "select s.mainImageUrl, s.name, r.rate, r.createdDate, r.content ";
        sql += "from Stores s ";
        sql += "join Reviews r on s.storeId = r.storeId and r.userId = :userId ";
        sql += "where r.status 'active' and r.report = true ";

        if (sort != null && !sort.isEmpty()) {
            sql += "order by ";
            switch (sort) {
                case "최신순":
                    sql += "st.modifiedDate DESC";
                    break;
                case "별점 높은 순":
                    sql += "s.rating DESC";
                    break;
                // 추가적인 정렬 조건
                case "흠 또 뭐있지":
                    sql += "rv.reviewCount DESC";
                    break;
            }
        }

        assert sort != null;
        Map<String, Object> param = Map.of(
                "userId", userId,
                "sort", sort
        );

        return jdbcTemplate.query(sql, param,
                (rs, rowNum) -> new GetMyReviewResponse(
                        rs.getString("mainImageUrl"),
                        rs.getTimestamp("name").toString(),
                        rs.getInt("rate"),
                        rs.getString("createdDate"),
                        rs.getString("content")
                ));
    }

    public void updateReview(PatchReviewRequest patchReviewRequest) {
        String sql = "update Reviews set rate=:rate, content=:content, status='작성이후' where reviewId=:reviewId";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("rate", patchReviewRequest.getRate());
        params.addValue("content", patchReviewRequest.getContent());
        params.addValue("reviewId", patchReviewRequest.getReviewId());

        jdbcTemplate.update(sql, params);
    }
}

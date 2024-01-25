package com.example.mocu.Dao;

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
        String sql = "select name, email, userImage, status from Users" +
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
                        rs.getString("profile_image"),
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

    public List<Long> getAllUserIds() {
        String sql = "select userId from Users";

        return jdbcTemplate.queryForList(sql, new MapSqlParameterSource(), Long.class);
    }
}
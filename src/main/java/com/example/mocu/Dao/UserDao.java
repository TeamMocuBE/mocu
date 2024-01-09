package com.example.mocu.Dao;

import com.example.mocu.Dto.GetUserResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
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
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("profile_image"),
                        rs.getString("status"))
        );
    }
}

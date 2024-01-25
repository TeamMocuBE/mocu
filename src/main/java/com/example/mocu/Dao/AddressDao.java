package com.example.mocu.Dao;

import com.example.mocu.Dto.address.GetAddressResponse;
import com.example.mocu.Dto.address.PostAddressRequest;
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
public class AddressDao {
    private final NamedParameterJdbcTemplate jdbcTemplate;

    public AddressDao(DataSource dataSource) {
        this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

    public long createAddress(Long userId, PostAddressRequest postAddressRequest) {
        String sql = "insert into Addresses(userId, name, address)" + "values(:userId, :name, :address";
        // SqlParameterSource 객체를 생성하여 파라미터를 매핑합니다.
        // BeanPropertySqlParameterSource는 PostAddressRequest의 프로퍼티를 사용합니다.
        // 추가적으로 userId도 매핑합니다.
        MapSqlParameterSource param = new MapSqlParameterSource()
                .addValue("userId", userId)
                .addValue("name", postAddressRequest.getName())
                .addValue("address", postAddressRequest.getAddress());

        // 쿼리 실행 및 생성된 키(예: 자동 생성된 ID) 반환
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(sql, param, keyHolder);

        // 생성된 주소의 ID 반환
        return Objects.requireNonNull(keyHolder.getKey()).longValue();
    }

    public List<GetAddressResponse> getAddress(Long userId) {
        String sql = "select name, address from Addresses " +
                "where userId like :userId";

        Map<String, Object> param = Map.of(
                "userId", "%" + userId + "%");

        return jdbcTemplate.query(sql, param,
                (rs, rowNum) -> new GetAddressResponse(
                        rs.getString("name"),
                        rs.getString("address")
                ));
    }
}

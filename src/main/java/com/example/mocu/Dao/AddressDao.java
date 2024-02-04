package com.example.mocu.Dao;

import com.example.mocu.Dto.address.GetAddressResponse;
import com.example.mocu.Dto.address.PatchUserAddressRequest;
import com.example.mocu.Dto.address.PostAddressRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
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
        String sql = "insert into Addresses(userId, name, address, latitude, longitude)" + "values(:userId, :name, :address, :latitude, :longitude)";
        // SqlParameterSource 객체를 생성하여 파라미터를 매핑합니다.
        // BeanPropertySqlParameterSource는 PostAddressRequest의 프로퍼티를 사용합니다.
        // 추가적으로 userId도 매핑합니다.
        MapSqlParameterSource param = new MapSqlParameterSource()
                .addValue("userId", userId)
                .addValue("name", postAddressRequest.getName())
                .addValue("address", postAddressRequest.getAddress())
                .addValue("latitude", postAddressRequest.getLatitude())
                .addValue("longitide", postAddressRequest.getLongitude());

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
                "userId", userId);

        return jdbcTemplate.query(sql, param,
                (rs, rowNum) -> new GetAddressResponse(
                        rs.getString("name"),
                        rs.getString("address")
                ));
    }

    public long modifyAddress(Long userId, PatchUserAddressRequest patchUserAddressRequest) {
        String sql = "UPDATE Users SET address = :address, latitude = :latitude, longitude = :longitude WHERE userId = :userId";

        Map<String, Object> param = Map.of(
                "userId", userId,
                "name", patchUserAddressRequest.getName(),
                "address", patchUserAddressRequest.getAddress(),
                "latitude", patchUserAddressRequest.getLatitude(),
                "longitude", patchUserAddressRequest.getLongitude()
        );

        int updatedRows = jdbcTemplate.update(sql, param);

        if (updatedRows > 0) {
            // 성공적으로 주소가 업데이트된 경우
            return userId;
        } else {
            // 업데이트가 실패한 경우 (예: 해당 userId가 없는 경우)
            // 적절한 예외 처리를 수행하거나, 실패를 나타내는 특정 값을 반환
            throw new RuntimeException("주소 업데이트 실패");
        }
    }
}

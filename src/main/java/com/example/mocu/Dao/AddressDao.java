package com.example.mocu.Dao;

import com.example.mocu.Dto.address.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
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
                .addValue("longitude", postAddressRequest.getLongitude());

        // 쿼리 실행 및 생성된 키(예: 자동 생성된 ID) 반환
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(sql, param, keyHolder);

        // 생성된 주소의 ID 반환
        return Objects.requireNonNull(keyHolder.getKey()).longValue();
    }

    public List<GetAddressResponse> getAddress(Long userId) {
        String sql = "select addressId, name, address, latitude, longitude from Addresses " +
                "where userId like :userId";

        Map<String, Object> param = Map.of(
                "userId", userId);

        return jdbcTemplate.query(sql, param,
                (rs, rowNum) -> new GetAddressResponse(
                        rs.getLong("addressId"),
                        rs.getString("name"),
                        rs.getString("address"),
                        rs.getDouble("latitude"),
                        rs.getDouble("longitude")
                ));
    }

    public long modifyAddress(Long userId, PatchUserAddressRequest patchUserAddressRequest, Long addressId) {
        String sql = "UPDATE Addresses SET name =:name, address = :address, latitude = :latitude, longitude = :longitude WHERE userId = :userId and addressId = :addressId";

        Map<String, Object> param = Map.of(
                "userId", userId,
                "name", patchUserAddressRequest.getName(),
                "address", patchUserAddressRequest.getAddress(),
                "latitude", patchUserAddressRequest.getLatitude(),
                "longitude", patchUserAddressRequest.getLongitude(),
                "addressId", addressId
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

    public SelectUserAddressResponse selectAddress(Long userId, PatchUserSetAddress patchUserSetAddress) {
        // 사용자의 모든 주소 상태를 '선택되지 않음'으로 초기화
        String sqlResetStatus = "UPDATE Addresses SET status = 'not-select' WHERE userId = :userId";
        Map<String, Object> paramReset = Map.of("userId", userId);
        jdbcTemplate.update(sqlResetStatus, paramReset);

        // 지정된 주소의 상태를 '선택됨'으로 설정
        String sqlSelectAddress = "UPDATE Addresses SET status = 'select' WHERE userId = :userId AND name = :addressName";
        Map<String, Object> paramSelect = Map.of(
                "userId", userId,
                "addressName", patchUserSetAddress.getAddressName()
        );

        int updated = jdbcTemplate.update(sqlSelectAddress, paramSelect);

        if (updated == 0) {
            // 주소가 업데이트되지 않았다면, 예외 처리
            throw new EmptyResultDataAccessException("업데이트할 주소가 없습니다.", 1);
        }

        // 업데이트된 주소의 상세 정보 검색 및 반환
        String sql = "SELECT addressId, name, status FROM Addresses WHERE userId = :userId AND name = :addressName";
        try {
            return jdbcTemplate.queryForObject(sql, paramSelect, (rs, rowNum) -> new SelectUserAddressResponse(
                    rs.getLong("addressId"),
                    rs.getString("name"),
                    rs.getString("status")
            ));
        } catch (EmptyResultDataAccessException ex) {
            // 주소가 존재하지 않거나 선택되지 않았을 경우 예외 처리
            throw new EmptyResultDataAccessException("예상대로 주소를 선택하는 데 실패했습니다.", 1);
        }
    }
}

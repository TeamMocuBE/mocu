package com.example.mocu.Dao;

import com.example.mocu.Dto.map.GetMapStoreInfoResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Slf4j
@Repository
public class MapDao {
    private final NamedParameterJdbcTemplate jdbcTemplate;

    public MapDao(DataSource dataSource){
        this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

    public List<GetMapStoreInfoResponse> getMapStoreInfoList(long userId, double latitude, double longitude, int distance) {
        String sql = "select s.name as storeName, s.mainImageUrl, s.category, "
    }
}

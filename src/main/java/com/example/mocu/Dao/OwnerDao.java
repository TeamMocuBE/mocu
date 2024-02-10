package com.example.mocu.Dao;

import com.example.mocu.Dto.menu.MenuInfo;
import com.example.mocu.Dto.owner.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Repository
public class OwnerDao {
    private final NamedParameterJdbcTemplate jdbcTemplate;

    public OwnerDao(DataSource dataSource) {
        this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

    public long registerStore(PostOwnerStoreRequest postOwnerStoreRequest) {
        String sql = "INSERT INTO Stores (ownerId, name, category, address, latitude, longitude, mainImageUrl, event, reward, maxStamp) " +
                "VALUES (:ownerId, :storeName, :category, :address, :latitude, :longitude, :mainImageUrl, :event, :reward, :maxStamp)";

        BeanPropertySqlParameterSource params = new BeanPropertySqlParameterSource(postOwnerStoreRequest);
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(sql, params, keyHolder);

        return Objects.requireNonNull(keyHolder.getKey()).longValue();
    }

    public void insertStoreImages(long storeId, List<String> storeImages) {
        String sql = "insert into StoreImage (storeId, imageUrl) values (:storeId, :imageUrl)";
        for(String imageUrl : storeImages){
            Map<String,Object> params = new HashMap<>();
            params.put("storeId", storeId);
            params.put("imageUrl", imageUrl);
            jdbcTemplate.update(sql, params);
        }
    }

    public void insertMenus(long storeId, List<MenuInfo> menus) {
        String sql = "insert into Menus (storeId, name, price, imageUrl) values (:storeId, :name, :price, :imageUrl)";
        for(MenuInfo menu : menus){
            Map<String, Object> params = new HashMap<>();
            params.put("storeId", storeId);
            params.put("name", menu.getName());
            params.put("price", menu.getPrice());
            params.put("imageUrl", menu.getImageUrl());
            jdbcTemplate.update(sql, params);
        }
    }

    public void updateStoreInfo(PatchOwnerStoreRequest patchOwnerStoreRequest) {
        String sql = "update Stores set name=:storeName, category=:category, address=:address, latitude=:latitude, longitude=:longitude, " +
                "mainImageUrl=:mainImageUrl, event=:event, reward=:reward, " +
                "maxStamp=:maxStamp where storeId=:storeId";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("storeName", patchOwnerStoreRequest.getStoreName());
        params.addValue("category", patchOwnerStoreRequest.getCategory());
        params.addValue("address", patchOwnerStoreRequest.getAddress());
        params.addValue("latitude", patchOwnerStoreRequest.getLatitude());
        params.addValue("longitude", patchOwnerStoreRequest.getLongitude());
        params.addValue("reward", patchOwnerStoreRequest.getReward());
        params.addValue("maxStamp", patchOwnerStoreRequest.getMaxStamp());
        params.addValue("mainImageUrl", patchOwnerStoreRequest.getMainImageUrl());
        params.addValue("event", patchOwnerStoreRequest.getEvent());
        params.addValue("storeId", patchOwnerStoreRequest.getStoreId());

        jdbcTemplate.update(sql, params);
    }

    public void updateStoreImages(long storeId, List<String> storeImages) {
        // 1. Delete existing storeImages
        String sql = "delete from StoreImage where storeId=:storeId";
        Map<String, Object> params = new HashMap<>();
        params.put("storeId", storeId);
        jdbcTemplate.update(sql, params);

        // 2. Insert new storeImages
        String insertSql = "insert into StoreImage (storeId, imageUrl) values (:storeId, :imageUrl)";
        for(String imageUrl : storeImages){
            params.put("imageUrl", imageUrl);
            jdbcTemplate.update(insertSql, params);
        }
    }

    public void updateMenus(long storeId, List<MenuInfo> menus) {
        // 1. Delete existing menus
        String sql = "delete from Menus where storeId=:storeId";
        Map<String, Object> params = new HashMap<>();
        params.put("storeId", storeId);
        jdbcTemplate.update(sql, params);

        // 2. Insert new menus
        String insertSql = "insert into Menus (storeId, name, price, imageUrl) values (:storeId, :name, :price, :imageUrl)";
        for(MenuInfo menuInfo : menus){
            params.put("storeId", storeId);
            params.put("name", menuInfo.getName());
            params.put("price", menuInfo.getPrice());
            params.put("imageUrl", menuInfo.getImageUrl());
            jdbcTemplate.update(insertSql, params);
        }
    }

    public GetOwnerStoreInfoResponse getStoreInfoForOwner(long storeId) {
        // 1. Stores table 정보 get
        String sql = "select name as storeName, category, address, reward, maxStamp, mainImageUrl, event from Stores where storeId=:storeId";
        Map<String, Object> params = new HashMap<>();
        params.put("storeId", storeId);
        GetOwnerStoreInfoResponse storeInfoResponse = jdbcTemplate.queryForObject(sql, params, new BeanPropertyRowMapper<>(GetOwnerStoreInfoResponse.class));

        // 2. StoreImage table 정보 get
        String imageSql = "select imageUrl from StoreImage where storeId=:storeId";
        List<String> storeImages = jdbcTemplate.queryForList(imageSql, params, String.class);
        storeInfoResponse.setStoreImages(storeImages);

        // 3. Menus table 정보 get
        String menuSql = "select name, price, imageUrl from Menus where storeId=:storeId";
        List<MenuInfo> menus = jdbcTemplate.query(menuSql, params, new BeanPropertyRowMapper<>(MenuInfo.class));
        storeInfoResponse.setMenus(menus);

        return storeInfoResponse;
    }


    private String timestampToString(Timestamp timestamp) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        return (timestamp != null) ? dateFormat.format(timestamp) : null;
    }


    public List<GetUserRequestForOwner> getNotAcceptedCouponRequests(long storeId, int page) {
        int limit = 5;
        int offset = page * limit;

        String sql = "select u.name, cr.status as acceptOption, 'reward' as StampOrReward, cr.modifiedDate " +
                "from Users u join CouponsRequest cr on u.userId=cr.userId where cr.storeId=:storeId and cr.status='not-accept' " +
                "order by cr.modifiedDate desc limit :limit offset :offset";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("storeId", storeId);
        params.addValue("limit", limit);
        params.addValue("offset", offset);

        return jdbcTemplate.query(sql, params, (rs, rowNum) -> new GetUserRequestForOwner(
                rs.getString("name"),
                rs.getString("acceptOption"),
                rs.getString("StampOrReward"),
                timestampToString(rs.getTimestamp("modifiedDate"))
            )
        );
    }

    public List<GetUserRequestForOwner> getNotAcceptedStampRequests(long storeId, int page) {
        int limit = 5;
        int offset = page * limit;

        String sql = "select u.name, sr.status as acceptOption, 'stamp' as StampOrReward, sr.modifiedDate " +
                "from Users u join StampsRequest sr on u.userId=sr.userId where sr.storeId=:storeId and sr.status='not-accept' " +
                "order by sr.modifiedDate desc limit :limit offset :offset";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("storeId", storeId);
        params.addValue("limit", limit);
        params.addValue("offset", offset);

        return jdbcTemplate.query(sql, params, (rs, rowNum) -> new GetUserRequestForOwner(
                rs.getString("name"),
                rs.getString("acceptOption"),
                rs.getString("StampOrReward"),
                timestampToString(rs.getTimestamp("modifiedDate"))
            )
        );
    }

    public List<GetUserRequestForOwner> getNotAcceptedBothRequests(long storeId, int page) {
        int limit = 5;
        int offset = page * limit;

        String sql = "select name, acceptOption, StampOrReward, modifiedDate from (" +
                "(select u.name, cr.status as acceptOption, 'reward' as StampOrReward, cr.modifiedDate " +
                "from Users u join CouponsRequest cr on u.userId=cr.userId " +
                "where cr.storeId=:storeId and cr.status='not-accept') " +
                "union all " +
                "(select u.name, sr.status as acceptOption, 'stamp' as StampOrReward, sr.modifiedDate " +
                "from Users u join StampsRequest sr on u.userId=sr.userId " +
                "where sr.storeId=:storeId and sr.status='not-accept')" +
                ") as combined order by modifiedDate desc limit :limit offset :offset";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("storeId", storeId);
        params.addValue("limit", limit);
        params.addValue("offset", offset);

        return jdbcTemplate.query(sql, params, (rs, rowNum) -> new GetUserRequestForOwner(
                rs.getString("name"),
                rs.getString("acceptOption"),
                rs.getString("StampOrReward"),
                timestampToString(rs.getTimestamp("modifiedDate"))
            )
        );
    }

    public List<GetUserRequestForOwner> getAllCouponRequests(long storeId, int page) {
        int limit = 5;
        int offset = page * limit;

        String sql = "select u.name, cr.status as acceptOption, 'reward' as StampOrReward, cr.modifiedDate " +
                "from Users u join CouponsRequest cr on u.userId=cr.userId " +
                "where cr.storeId=:storeId order by cr.modifiedDate desc limit :limit, offset :offset";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("storeId", storeId);
        params.addValue("limit", limit);
        params.addValue("offset", offset);

        return jdbcTemplate.query(sql, params, (rs, rowNum) -> new GetUserRequestForOwner(
                rs.getString("name"),
                rs.getString("acceptOption"),
                rs.getString("StampOrReward"),
                timestampToString(rs.getTimestamp("modifiedDate"))
            )
        );
    }
  
    //TODO. modify useCount
    public List<GetCustomerStampResponse> getCustomerStamp(Long ownerId, boolean isCustomerRegular, String sort) {
        String sql = "select u.userImage, u.name, st.numOfStamp, s.maxStamp, st.useCount ";
        sql += "from Stores s ";
        sql += "join Stamps st on s.storeId = st.storeId ";
        sql += "join Users u on st.userId = u.userId ";

        if (isCustomerRegular) {
            sql += "join Regulars r on u.userId = r.userId ";
            sql += "where r.status = 'accept' ";
            sql += "and s.ownerId = :ownerId ";
        } else {
            sql += "where s.ownerId = :ownerId ";
        }

        if (sort != null && !sort.isEmpty()) {
            sql += "order by ";
            switch (sort) {
                case("적립 많은 순") -> {
                    sql += "st.numOfStamp";
                    break;
                }
                case ("쿠폰 사용 많은 순") -> {
                    sql += "st.useCount";
                    break;
                }
                case ("최근 방문 순") -> {
                    sql += "st.createdDate DESC";
                }
            }
        }

        Map<String, Object> param = Map.of(
                "ownerId", ownerId
        );

        return jdbcTemplate.query(sql, param,
                (rs, rowNum) -> new GetCustomerStampResponse(
                        rs.getString("userImage"),
                        rs.getString("name"),
                        rs.getInt("numOfStamp"),
                        rs.getInt("maxStamp"),
                        rs.getInt("useCount")
                ));
      }
  
    public List<GetUserRequestForOwner> getAllStampRequests(long storeId, int page) {
        int limit = 5;
        int offset = page * limit;

        String sql = "select u.name, sr.status as acceptOption, 'stamp' as StampOrReward, sr.modifiedDate " +
                "from Users u join StampsRequest sr on u.userId=sr.userId " +
                "where sr.storeId=:storeId order by sr.modifiedDate desc limit :limit offset :offset";

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("storeId", storeId);
        params.addValue("limit", limit);
        params.addValue("offset", offset);

        return jdbcTemplate.query(sql, params, (rs, rowNum) -> new GetUserRequestForOwner(
                        rs.getString("name"),
                        rs.getString("acceptOption"),
                        rs.getString("StampOrReward"),
                        timestampToString(rs.getTimestamp("modifiedDate"))
                )
        );
    }

    public List<GetUserRequestForOwner> getAllBothRequests(long storeId, int page) {
        int limit = 5;
        int offset = page * limit;

        String sql = "select name, acceptOption, StampOrReward, modifiedDate from (" +
                "(select u.name, cr.status as acceptOption, 'reward' as StampOrReward, cr.modifiedDate " +
                "from Users u join CouponsRequest cr on u.userId=cr.userId " +
                "where cr.storeId=:storeId) " +
                "union all " +
                "(select u.name, sr.status as acceptOption, 'stamp' as StampOrReward, sr.modifiedDate " +
                "from Users u join StampsRequest sr on u.userId=sr.userId " +
                "where sr.storeId=:storeId)" +
                ") as combined order by modifiedDate desc limit :limit offset :offset";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("storeId", storeId);
        params.addValue("limit", limit);
        params.addValue("offset", offset);

        return jdbcTemplate.query(sql, params, (rs, rowNum) -> new GetUserRequestForOwner(
                        rs.getString("name"),
                        rs.getString("acceptOption"),
                        rs.getString("StampOrReward"),
                        timestampToString(rs.getTimestamp("modifiedDate"))
                )
        );
    }
}

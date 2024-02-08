package com.example.mocu.Dao;

import com.example.mocu.Dto.menu.MenuInfo;
import com.example.mocu.Dto.owner.GetCustomerStampResponse;
import com.example.mocu.Dto.owner.GetOwnerStampNotAcceptResponse;
import com.example.mocu.Dto.owner.PatchOwnerStoreRequest;
import com.example.mocu.Dto.owner.PostOwnerStoreRequest;
import lombok.extern.slf4j.Slf4j;
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
        String sql = "INSERT INTO Stores (ownerId, name, category, address, coordinate, reward, maxStamp) " +
                "VALUES (:ownerId, :storeName, :category, :address, :coordinate, :reward, :maxStamp)";

        SqlParameterSource param = new BeanPropertySqlParameterSource(postOwnerStoreRequest);
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(sql, param, keyHolder);

        long storeId = Objects.requireNonNull(keyHolder.getKey()).longValue();

        // 이미지 url list insert
        if(postOwnerStoreRequest.getStoreImages() != null && !postOwnerStoreRequest.getStoreImages().isEmpty()){
            for(String imageUrl : postOwnerStoreRequest.getStoreImages()){
                MapSqlParameterSource imageParameters = new MapSqlParameterSource();
                imageParameters.addValue("storeId", storeId);
                imageParameters.addValue("imageUrl", imageUrl);

                SimpleJdbcInsert imageInsert = new SimpleJdbcInsert(jdbcTemplate.getJdbcTemplate())
                        .withTableName("StoreImage")
                        .usingGeneratedKeyColumns("storeImageId");

                imageInsert.execute(imageParameters);
            }
        }

        // 메뉴 추가
        if(postOwnerStoreRequest.getMenus() != null && !postOwnerStoreRequest.getMenus().isEmpty()){
            String menuSql = "insert into Menus (storeId, name, price, imageUrl) " +
                    "values (:storeId, :name, :price, :imageUrl)";

            List<MapSqlParameterSource> menuParamsList = postOwnerStoreRequest.getMenus().stream()
                    .map(menu -> {
                        MapSqlParameterSource menuParams = new MapSqlParameterSource();
                        menuParams.addValue("storeId", storeId);
                        menuParams.addValue("name", menu.getName());
                        menuParams.addValue("price", menu.getPrice());
                        menuParams.addValue("imageUrl", menu.getMenuImageUrl());
                        return menuParams;
                    })
                    .toList();
            jdbcTemplate.batchUpdate(menuSql, menuParamsList.toArray(new MapSqlParameterSource[0]));
        }

        return storeId;
    }

    public void modifyStoreInfo(long storeId, PatchOwnerStoreRequest patchOwnerStoreRequest) {
        String sql = "update Stores set storeName=:storeName, category=:category, " +
                "address=:address, coordinate=:coordinate, reward=:reward, " +
                "maxStamp=:numOfStamp where storeId=:storeId";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("storeName", patchOwnerStoreRequest.getStoreName());
        params.addValue("category", patchOwnerStoreRequest.getCategory());
        params.addValue("address", patchOwnerStoreRequest.getAddress());
        params.addValue("coordinate", patchOwnerStoreRequest.getCoordinate());
        params.addValue("reward", patchOwnerStoreRequest.getReward());
        params.addValue("numOfStamp", patchOwnerStoreRequest.getNumOfStamp());
        params.addValue("storeId", storeId);

        jdbcTemplate.update(sql, params);

        // 이미지 url을 StoreImage 테이블에 추가 또는 수정
        if(patchOwnerStoreRequest.getStoreImages() != null && !patchOwnerStoreRequest.getStoreImages().isEmpty()){
            // 기존 이미지 정보 삭제
            String deleteImages = "delete from StoreImage where storeId=:storeId";
            jdbcTemplate.update(deleteImages, params);

            // 새로운 이미지 정보 추가
            String insertImages = "insert into StoreImage (storeId, imageUrl) values (:storeId, :imageUrl)";
            for(String imageUrl : patchOwnerStoreRequest.getStoreImages()){
                MapSqlParameterSource imageParams = new MapSqlParameterSource();
                imageParams.addValue("storeId", storeId);
                imageParams.addValue("imageUrl", imageUrl);

                jdbcTemplate.update(insertImages, imageParams);
            }
        }

        // 메뉴 정보 업데이트
        if(patchOwnerStoreRequest.getMenus() != null && !patchOwnerStoreRequest.getMenus().isEmpty()){
            // 기존 메뉴 정보 삭제
            String deleteMenus = "delete from Menus where storeId=:storeId";
            jdbcTemplate.update(deleteMenus, params);

            // 새로운 메뉴 정보 추가
            String insertMenus = "insert into Menus (storeId, name, price, imageUrl) " +
                    "values (:storeId, :name, :price, :imageUrl)";
            for(MenuInfo menu : patchOwnerStoreRequest.getMenus()){
                MapSqlParameterSource menuParams = new MapSqlParameterSource();
                menuParams.addValue("storeId", storeId);
                menuParams.addValue("name", menu.getName());
                menuParams.addValue("price", menu.getPrice());
                menuParams.addValue("imageUrl", menu.getMenuImageUrl());

                jdbcTemplate.update(insertMenus, menuParams);
            }
        }

    }

    public List<GetOwnerStampNotAcceptResponse> getStampsNotAccept(long storeId) {
        String sql = "select u.name as userName, sr.createdDate as createdDate, sr.status as status " +
                "from StampsRequest sr inner join Users u on sr.userId=u.userId " +
                "where sr.storeId=:storeId and sr.status='not-accept'";

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("storeId", storeId);

        return jdbcTemplate.query(sql, params, (rs, rowNum) -> new GetOwnerStampNotAcceptResponse(
                rs.getString("userName"),
                timestampToString(rs.getTimestamp("createdDate")),
                rs.getString("status"))
        );
    }

    private String timestampToString(Timestamp timestamp) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        return (timestamp != null) ? dateFormat.format(timestamp) : null;
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
}

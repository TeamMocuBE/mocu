package com.example.mocu.Dto.owner;

import com.example.mocu.Dto.menu.MenuInfo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PatchOwnerStoreRequest {
    private String storeName;
    private String category;
    private String address;
    private String coordinate;
    private String reward;
    private int numOfStamp;         // 쿠폰 사용이 가능한 스탬프 수
    private List<String> storeImages;
    private List<MenuInfo> menus;
}

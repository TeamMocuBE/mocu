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
public class GetOwnerStoreInfoResponse {
    private String storeName;
    private String category;
    private String address;
    private String reward;
    private int maxStamp;
    private String mainImageUrl;
    private List<String> storeImages;
    private List<MenuInfo> menus;
    private String event;
}

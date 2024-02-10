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
public class PostOwnerStoreRequest {
    private long ownerId;
    private String storeName;
    private String category;
    private String address;
    private double latitude;
    private double longitude;
    private String reward;
    private int maxStamp;             // 쿠폰 사용이 가능한 스탬프 수
    private String mainImageUrl;
    private List<String> storeImages;
    private List<MenuInfo> menus;
    private String event;             // 이벤트 내용(없으면 null)
}



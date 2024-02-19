package com.example.mocu.Dto.store;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StoreInEventInfo {
    // storeId, 이벤트 중인 가게의 storeName, 대표이미지 return
    private long storeId;
    private String storeName;
    private String mainImageUrl;
}

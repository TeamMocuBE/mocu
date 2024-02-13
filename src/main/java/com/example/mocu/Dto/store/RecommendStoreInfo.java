package com.example.mocu.Dto.store;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RecommendStoreInfo {
    // 추천 가게 이름, 이벤트 여부, 가게 대표 사진
    private String storeName;
    private boolean hasEvent;
    private String mainImageUrl;
}

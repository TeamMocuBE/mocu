package com.example.mocu.Dto.store;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RecentlyVisitedStoreInfo {
    // 가게 이름, 사용 가능한 쿠폰 개수, 가게 좌표, 이벤트 진행 여부
    private String storeName;
    private int numOfCouponAvailable;
    private boolean event;
    private String coordinate;
}

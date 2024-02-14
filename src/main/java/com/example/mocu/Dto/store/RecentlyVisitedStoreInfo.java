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
    // 가게 이름, 스탬프 수, 사용 가능한 쿠폰 개수, 이벤트 진행 여부, 현 유저위치에서 가게까지의 거리
    private String storeName;
    private int numOfStamp;
    private int numOfCouponAvailable;
    private boolean hasEvent;
    private double distance;
}

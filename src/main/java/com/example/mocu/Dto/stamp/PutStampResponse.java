package com.example.mocu.Dto.stamp;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PutStampResponse {
    private long stampId;

    // 스탬프 적립 후 보유 스탬프 수
    private int numberOfStamps;
    // 스탬프 적립 후 보유 쿠폰 수
    private int numberOfCoupons;
}

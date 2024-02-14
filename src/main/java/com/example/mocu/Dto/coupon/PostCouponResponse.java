package com.example.mocu.Dto.coupon;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PostCouponResponse {
    // couponRequestId, userId, storeId, 쿠폰사용요청 생성일, 쿠폰사용요청 가게 주소, 유저 이름
    private long couponRequestId;
    private long userId;
    private long storeId;
    private String createdDate;
    private String storeAddress;
    private String userName;
}

package com.example.mocu.Dto.coupon;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PostCouponRequest {
    // user가 쿠폰 사용 요청 버튼을 누르면
    // 쿠폰 사용 요청 table에 "not-accept" 상태를 가지는 data가 생성
    // 점주 앱에서 적립 요청을 수락하면 해당 data가 "accept"상태로 변경
    private long userId;
    private long storeId;
}

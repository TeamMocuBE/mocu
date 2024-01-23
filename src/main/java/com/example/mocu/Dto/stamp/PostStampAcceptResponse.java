package com.example.mocu.Dto.stamp;

import jakarta.servlet.ServletResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PostStampAcceptResponse {
    // stampId, 적립후 스탬프 개수, 가게의 쿠폰등록가능한 스탬프 개수, 가게 이름
    // 쿠폰 임박 여부 -> 지도페이지 아이콘 표시
    // 사용가능한 쿠폰 개수
    private long stampId;
    private int numOfStamp;
    private int maxStamp;
    private String storeName;
    private boolean dueDate;
    private int numOfCouponAvailable;
}

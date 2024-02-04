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
    // '이벤트 중인 가게에서 적립하기' 또는
    // '단골가게에서 적립하기' 가 오늘의 미션인지 check
    // missionComplete = false : '리뷰 작성하기'가 오늘의 미션 X
    //                 = true : '리뷰 작성하기'가 오늘의 미션 O
    private long stampId;
    private int numOfStamp;
    private int maxStamp;
    private String storeName;
    private boolean dueDate;
    private int numOfCouponAvailable;
    private boolean missionComplete;
}

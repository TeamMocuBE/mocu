package com.example.mocu.Dto.coupon;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PostCouponAcceptResponse {
    // stampId, 쿠폰 사용후 스탬프 개수, 가게의 쿠폰등록가능한 스탬프 개수, 가게 이름, 보상
    // 쿠폰 임박 여부 -> 지도페이지 아이콘 표시
    // 사용가능한 쿠폰 개수
    // '쿠폰 사용하기'가 오늘의 미션인지 check
    // missionComplete = false : '리뷰 작성하기'가 오늘의 미션 X
    //                 = true : '리뷰 작성하기'가 오늘의 미션 O
    // regularPopUp -> '이 가게를 단골로 설정하시겠습니까' 팝업창 띄울지 말지 결정
    // -> true : 팝업창 띄우기 , false : 팝업창 띄우지 않기
    private long stampId;
    private int numOfStamp;
    private int maxStamp;
    private String storeName;
    private String reward;
    private boolean dueDate;
    private int numOfCouponAvailable;
    private boolean missionComplete;
    private boolean regularPopUp;
}

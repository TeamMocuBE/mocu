package com.example.mocu.Dto.coupon;

import com.example.mocu.Dto.mission.IsTodayMission;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PostCouponAcceptResponse {
    // stampId, 쿠폰 사용후 스탬프 개수, 가게의 쿠폰등록가능한 스탬프 개수, 가게 이름, 보상
    // 쿠폰 사용 후 쿠폰 임박 여부 -> 지도페이지 아이콘 표시
    // 쿠폰 사용 후 사용가능한 쿠폰 개수
    // 오늘의 미션에 해당하는 쿠폰 사용 기능 List return
    // regularPopUp -> '이 가게를 단골로 설정하시겠습니까' 팝업창 띄울지 말지 결정
    // -> true : 팝업창 띄우기 , false : 팝업창 띄우지 않기
    private long stampId;
    private int numOfStamp;
    private int maxStamp;
    private String storeName;
    private String reward;
    private boolean dueDate;
    private int numOfCouponAvailable;
    private List<IsTodayMission> todayMissionList;
    private boolean regularPopUp;
}

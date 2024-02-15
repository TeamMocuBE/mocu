package com.example.mocu.Dto.owner;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GetUserRequestForOwner {
    // userName, 수락/미수락 여부, 적립요청인지 보상요청인지 구분
    // 요청들어온 시각 -> 최신순으로 정렬해서 response 보냄
    private String name;
    private String acceptOption;
    private String StampOrReward;
    private String modifiedDate;
}

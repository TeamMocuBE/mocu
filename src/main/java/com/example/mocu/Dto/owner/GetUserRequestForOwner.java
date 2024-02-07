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
    // userName, 수락/미수락 여부, 적립요청인지 보상요청인지 구분, 시간
    private String name;
    private String acceptOption;
    private String StampOrReward;
    private String modifiedDate;
}

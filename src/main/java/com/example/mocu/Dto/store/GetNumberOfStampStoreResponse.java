package com.example.mocu.Dto.store;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GetNumberOfStampStoreResponse {
    // 해당 유저가 해당 가게에 적립한 스탬프 수
    private int numOfStamps;
}

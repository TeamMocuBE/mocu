package com.example.mocu.Dto.map;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GetMapStoreResponse {
    // isDueDate : ture -> 유저가 해당 가게에 쿠폰 사용 임박 상태임 / false -> X
    private long storeId;
    private double latitude;
    private double longitude;
    private String category;
    private boolean hasEvent;
    private boolean isDueDate;
}

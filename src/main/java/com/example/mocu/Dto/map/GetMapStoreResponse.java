package com.example.mocu.Dto.map;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.util.pattern.PathPattern;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GetMapStoreResponse {
    // isDueDate : true -> 유저가 해당 가게에 쿠폰 사용 임박 상태임 / false -> X
    // isVisited : true -> 유저가 해당 가게에 스탬ㅍ 적립 한적 있음 / false -> X
    private long storeId;
    private double latitude;
    private double longitude;
    private String category;
    private boolean hasEvent;
    private boolean isDueDate;
    private boolean isVisited;
}

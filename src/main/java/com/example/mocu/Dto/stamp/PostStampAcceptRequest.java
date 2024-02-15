package com.example.mocu.Dto.stamp;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PostStampAcceptRequest {
    // userId, storeId, stampRequestId
    // -> 점주앱으로 푸시알림보낼때 데이터 영역에 들어가는 값
    // -> 프론트단에서 가지고 있다가 점주가 적립요청을 수락하면 requestBody에 그대로 넘겨주면 될듯
    private long userId;
    private long storeId;
    private long stampRequestId;
    private int numOfStamp;
}

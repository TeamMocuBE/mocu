package com.example.mocu.Dto.stamp;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PostStampResponse {
    // stampRequestId, 스탬프적립요청 생성일, 스탬프적립요청 가게 주소, 유저 이름
    private long stampRequestId;
    private String createdDate;
    private String storeAddress;
    private String userName;
}

package com.example.mocu.Dto.stamp;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PutStampRequest {
    private long userId;
    private long storeId;
    private int numberOfStamp;          // 적립허용 스탬프 수
}

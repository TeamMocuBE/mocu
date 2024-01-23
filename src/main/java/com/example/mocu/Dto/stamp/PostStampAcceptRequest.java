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
    private long userId;
    private long storeId;
    private long stampRequestId;
    private int numOfStamp;
}

package com.example.mocu.Dto.Push;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class PushRequestToUser {
    private String userUuid;
    private long stampId;
    private int numOfStamp;
    private int maxStamp;
    private String storeName;
    private boolean dueDate;
    private int numOfCouponAvailable;
}

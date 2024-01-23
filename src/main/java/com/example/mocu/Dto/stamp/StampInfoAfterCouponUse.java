package com.example.mocu.Dto.stamp;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StampInfoAfterCouponUse {
    private long stampId;
    private int numOfStamp;
    private int numOfCouponAvailable;
}

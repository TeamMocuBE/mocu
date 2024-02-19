package com.example.mocu.Dto.coupon;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GetMyCouponList {
    private String mainImageUrl;

    private String name;

    private int maxStamp;

    private int numOfStamp;

    private String reward;

    private double latitude;

    private double longitude;

    private String event;

    private float rating;
}

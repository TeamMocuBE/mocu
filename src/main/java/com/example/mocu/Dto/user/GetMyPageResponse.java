package com.example.mocu.Dto.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GetMyPageResponse {
    private int usableCoupon;

    private int availableFavoriteCount;

    private String currentAddress;

    private List<CouponUsageDetail> recentCouponUsage;

    private int availableReviewCount;

    private int missionStampCount;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CouponUsageDetail {
        private String benefit;
        private String storeName;
    }

}

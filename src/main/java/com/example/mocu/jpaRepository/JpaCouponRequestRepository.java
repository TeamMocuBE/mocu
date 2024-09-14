package com.example.mocu.jpaRepository;

import com.example.mocu.Dto.user.GetMyPageResponse;
import com.example.mocu.domain.CouponsRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface JpaCouponRequestRepository extends JpaRepository<CouponsRequest, Long> {
    @Query("select new com.example.mocu.Dto.user.GetMyPageResponse.CouponUsageDetail(s.reward, s.name)" +
            " from CouponsRequest cr join cr.store s" +
            " where cr.user = :userId and cr.status = 'accept'" +
            " and cr.createdDate >= :oneMonthAgo" +
            " order by cr.createdDate desc" +
            " limit 5")
    List<GetMyPageResponse.CouponUsageDetail> findRecentCouponUsage(Long userId, LocalDate oneMonthAgo);
}

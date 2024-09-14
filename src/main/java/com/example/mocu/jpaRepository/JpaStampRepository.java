package com.example.mocu.jpaRepository;

import com.example.mocu.domain.Stamp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaStampRepository extends JpaRepository<Stamp, Long> {
    @Query("select sum(s.numOfCouponAvailable) from Stamp s where s.user.id = :userId and s.status = 'active'")
    Integer findUsableCouponsById(Long userId);
}

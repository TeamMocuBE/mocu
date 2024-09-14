package com.example.mocu.jpaRepository;

import com.example.mocu.domain.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaReviewRepository extends JpaRepository<Review, Long> {
    @Query("select count(*) from Review r where r.user = :userId and r.status = '작성이전'")
    Integer findAvailableReviewCount(Long userId);
}

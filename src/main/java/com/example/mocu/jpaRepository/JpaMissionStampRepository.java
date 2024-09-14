package com.example.mocu.jpaRepository;

import com.example.mocu.domain.MissionStamp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaMissionStampRepository extends JpaRepository<MissionStamp, Long> {
    @Query("select ms.numOfStamp from MissionStamp ms where ms.user = :userId")
    Integer findMissionStampCount(Long userId);
}

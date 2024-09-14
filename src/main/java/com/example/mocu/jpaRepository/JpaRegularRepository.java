package com.example.mocu.jpaRepository;

import com.example.mocu.domain.Regular;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaRegularRepository extends JpaRepository<Regular, Long> {
    @Query("select count(r) from Regular r where r.status = 'request' and r.user.id = :userId")
    Integer countAvailableFavoriteStores(Long userId);
}

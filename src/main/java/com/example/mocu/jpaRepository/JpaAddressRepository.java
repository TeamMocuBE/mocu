package com.example.mocu.jpaRepository;

import com.example.mocu.domain.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JpaAddressRepository extends JpaRepository<Address, Long> {
    @Query("select a.address from Address a where a.status = 'select'")
    Optional<String> findCurrentAddress();
}

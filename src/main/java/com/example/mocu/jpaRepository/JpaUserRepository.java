package com.example.mocu.jpaRepository;

import com.example.mocu.Dto.user.GetUserResponse;
import com.example.mocu.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JpaUserRepository extends JpaRepository<User, Long> {
    @Query("select new com.example.mocu.Dto.user.GetUserResponse(u.id, u.name, u.email, u.userImage, u.status, u.provider)" +
            " from User u where u.name like :name and u.email = :email and u.status = :status")
    List<GetUserResponse> findUsersDto(String name, String email, String status);
}

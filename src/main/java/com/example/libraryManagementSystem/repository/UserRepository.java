package com.example.libraryManagementSystem.repository;

import com.example.libraryManagementSystem.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> getUserByUserId(Long userId);
    Optional<User> findByEmail(String email);


    // Native query to fetch users based on pagination
//    @Query(value = "SELECT * FROM user LIMIT :4 OFFSET : 6", nativeQuery = true)
//    Page<User> findAllWithPagination(Pageable pageable);

}

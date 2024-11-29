package com.example.libraryManagementSystem.repository;

import com.example.libraryManagementSystem.entity.Fine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FineRepository extends JpaRepository<Fine,Long> {
    Optional<Fine> getFineByFineId(Long fineId);
}

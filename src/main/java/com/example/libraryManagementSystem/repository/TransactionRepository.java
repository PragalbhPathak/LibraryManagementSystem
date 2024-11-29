package com.example.libraryManagementSystem.repository;

import com.example.libraryManagementSystem.entity.Transaction;
import com.example.libraryManagementSystem.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction,Long> {
    Optional<Transaction> getTransactionByTransactionId(Long transactionId);
}

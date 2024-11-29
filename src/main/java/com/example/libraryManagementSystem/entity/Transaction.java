package com.example.libraryManagementSystem.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDate;
import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "transaction")
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "transactionId")
    private Long transactionId;
    @Column(name = "userId")
    private Long userId;
    @Column(name = "bookId")
    private Long bookId;
    @Column(name = "borrowDate")
    private LocalDate borrowDate;
    @Column(name = "returnDate")
    private LocalDate returnDate;
    @Column(name = "dueDate")
    private LocalDate dueDate;
    @Column(name = "status")
    private String status;  // e.g., Borrowed, Returned, Overdue

}

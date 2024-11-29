package com.example.libraryManagementSystem.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "fine")
public class Fine {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "fineId")
    private Long fineId;
    @Column(name = "transactionId")
    private Long transactionId;
    @Column(name = "amount")
    private Long amount;
    @Column(name = "paid")
    private boolean paid;
}

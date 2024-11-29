package com.example.libraryManagementSystem.dto.requestDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FineRequest {
    private Long fineId;
    private Long transactionId;
    private Long amount;
    private boolean paid;
}

package com.example.libraryManagementSystem.dto.responseDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FineResponse {
    private Long fineId;
    private Long transactionId;
    private Long amount;
    private boolean paid;
}

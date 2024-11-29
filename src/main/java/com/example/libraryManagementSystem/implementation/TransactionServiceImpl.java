package com.example.libraryManagementSystem.implementation;

import com.example.libraryManagementSystem.dto.requestDto.AuthRequest;
import com.example.libraryManagementSystem.dto.requestDto.TransactionRequest;
import com.example.libraryManagementSystem.dto.responseDto.BaseApiResponse;

public interface TransactionServiceImpl {
    BaseApiResponse updateTransaction(TransactionRequest transactionRequest);
    BaseApiResponse getTransactionByTransactionId(Long transactionId,boolean findAll);
}

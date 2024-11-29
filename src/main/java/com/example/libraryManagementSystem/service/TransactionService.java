package com.example.libraryManagementSystem.service;

import com.example.libraryManagementSystem.dto.requestDto.TransactionRequest;
import com.example.libraryManagementSystem.dto.responseDto.BaseApiResponse;
import com.example.libraryManagementSystem.dto.responseDto.TransactionResponse;
import com.example.libraryManagementSystem.entity.Transaction;
import com.example.libraryManagementSystem.implementation.TransactionServiceImpl;
import com.example.libraryManagementSystem.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.example.libraryManagementSystem.constraint.REST_MAPPING_CONSTRAINT.COMMON_MESSAGE.*;
import static com.example.libraryManagementSystem.constraint.REST_MAPPING_CONSTRAINT.STATUS_CODES.*;
import static com.example.libraryManagementSystem.constraint.REST_MAPPING_CONSTRAINT.SUCCESS_STATUS.FAILURE;
import static com.example.libraryManagementSystem.constraint.REST_MAPPING_CONSTRAINT.SUCCESS_STATUS.SUCCESS;

@Service
public class TransactionService implements TransactionServiceImpl {

    @Autowired
    private TransactionRepository transactionRepository;

    @Transactional
    public BaseApiResponse updateTransaction(TransactionRequest transactionRequest) {
        Transaction transaction = new Transaction();

        try {
            Optional<Transaction> existingTransaction = transactionRepository.findById(transactionRequest.getTransactionId());
            // Validate the return date before updating
            if (transactionRequest.getReturnDate() != null && transactionRequest.getReturnDate().isBefore(transactionRequest.getBorrowDate())) {
                return new BaseApiResponse(BAD_REQUEST, FAILURE, "Return date cannot be earlier than borrow date", Collections.emptyList());
            }
            // Validate the due date before updating
            if (transactionRequest.getDueDate() != null && transactionRequest.getDueDate().isBefore(transactionRequest.getBorrowDate())) {
                return new BaseApiResponse(BAD_REQUEST, FAILURE, "Due date cannot be earlier than borrow date", Collections.emptyList());
            }
            // Set due date if not provided
            if (transactionRequest.getDueDate() == null ) {
                LocalDate dueDate = calculateDueDate(transactionRequest.getBorrowDate());
                transactionRequest.setDueDate(dueDate);
            }
            // Ensure due date is at least 14 days after borrow date
            if (transactionRequest.getDueDate().isBefore(transactionRequest.getBorrowDate().plusDays(14))) {
                return new BaseApiResponse(BAD_REQUEST, FAILURE, "Due date should be at least 14 days after borrow date", Collections.emptyList());
            }
            // Process existing transaction or create a new one
            if (existingTransaction.isPresent()) {
                transaction = existingTransaction.get();
                transaction.setUserId(transactionRequest.getUserId());
                transaction.setBookId(transactionRequest.getBookId());
                transaction.setBorrowDate(transactionRequest.getBorrowDate());
                transaction.setReturnDate(transactionRequest.getReturnDate());
                transaction.setDueDate(transactionRequest.getDueDate());
                transaction.setStatus(transactionRequest.getStatus());

                transaction = transactionRepository.save(transaction);
            } else {
                transaction.setTransactionId(transactionRequest.getTransactionId());
                transaction.setUserId(transactionRequest.getUserId());
                transaction.setBookId(transactionRequest.getBookId());
                transaction.setBorrowDate(transactionRequest.getBorrowDate());
                transaction.setReturnDate(transactionRequest.getReturnDate());
                transaction.setDueDate(transactionRequest.getDueDate());
                transaction.setStatus(transactionRequest.getStatus());

                transaction = transactionRepository.save(transaction);
            }
            // Map the transaction to a response DTO
            TransactionResponse transactionResponse = mapToTransactionResponse(transaction);

            return new BaseApiResponse(SUCCESS_OK, SUCCESS, MESSAGE_SAVE, transactionResponse);

        } catch (Exception e) {
            return new BaseApiResponse(INTERNAL_SERVER_ERROR, FAILURE, MESSAGE_COMMON_SERVER_ERROR, Collections.emptyList());
        }
    }

    // Method to calculate the due date (14 days after borrow date)
    private LocalDate calculateDueDate(LocalDate borrowDate) {
        return borrowDate.plusDays(14); // Add 14 days to borrowDate
    }

    // Helper method to map Transaction to TransactionResponse DTO
    private TransactionResponse mapToTransactionResponse(Transaction transaction) {
        TransactionResponse transactionResponse = new TransactionResponse();
        transactionResponse.setTransactionId(transaction.getTransactionId());
        transactionResponse.setUserId(transaction.getUserId());
        transactionResponse.setBookId(transaction.getBookId());
        transactionResponse.setBorrowDate(transaction.getBorrowDate());
        transactionResponse.setReturnDate(transaction.getReturnDate());
        transactionResponse.setDueDate(transaction.getDueDate());
        transactionResponse.setStatus(transaction.getStatus());
        return transactionResponse;
    }

    // New method to handle fetching all transactions
    private BaseApiResponse getAllTransaction() {
        try {
            List<Transaction> transactions = transactionRepository.findAll();
            List<TransactionResponse> transactionResponses = transactions.stream()
                    .map(this::mapToTransactionResponse)
                    .collect(Collectors.toList());
            return new BaseApiResponse(SUCCESS_OK, SUCCESS, MESSAGE_FETCH_DATA, transactionResponses);
        } catch (Exception e) {
            return new BaseApiResponse(INTERNAL_SERVER_ERROR, FAILURE, MESSAGE_COMMON_SERVER_ERROR, Collections.emptyList());
        }
    }

    public BaseApiResponse getTransactionByTransactionId(Long transactionId, boolean findAll) {
        try {
            if (findAll) {
                return getAllTransaction();
            } else {
                Optional<Transaction> transactionOptional = transactionRepository.findById(transactionId);
                if (transactionOptional.isPresent()) {
                    Transaction transaction = transactionOptional.get();
                    TransactionResponse transactionResponse = mapToTransactionResponse(transaction);
                    return new BaseApiResponse(SUCCESS_OK, SUCCESS, MESSAGE_FETCH_DATA, transactionResponse);
                } else {
                    return new BaseApiResponse(NOT_FOUND, FAILURE, MESSAGE_NOT_FOUND, Collections.emptyList());
                }
            }
        } catch (Exception e) {
            return new BaseApiResponse(INTERNAL_SERVER_ERROR, FAILURE, MESSAGE_COMMON_SERVER_ERROR, Collections.emptyList());
        }
    }

}

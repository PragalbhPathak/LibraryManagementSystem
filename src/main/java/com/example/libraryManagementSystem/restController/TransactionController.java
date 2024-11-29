package com.example.libraryManagementSystem.restController;

import com.example.libraryManagementSystem.constraint.REST_MAPPING_CONSTRAINT;
import com.example.libraryManagementSystem.dto.requestDto.SearchRequest;
import com.example.libraryManagementSystem.dto.requestDto.TransactionRequest;
import com.example.libraryManagementSystem.dto.responseDto.BaseApiResponse;
import com.example.libraryManagementSystem.implementation.TransactionServiceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.Collections;

import static com.example.libraryManagementSystem.constraint.REST_MAPPING_CONSTRAINT.BASE_URL;
import static com.example.libraryManagementSystem.constraint.REST_MAPPING_CONSTRAINT.COMMON_MESSAGE.*;
import static com.example.libraryManagementSystem.constraint.REST_MAPPING_CONSTRAINT.STATUS_CODES.*;
import static com.example.libraryManagementSystem.constraint.REST_MAPPING_CONSTRAINT.SUCCESS_STATUS.FAILURE;

@RestController
@RequestMapping(BASE_URL)
public class TransactionController {

    @Autowired
    private TransactionServiceImpl transactionServiceImpl;

    @PreAuthorize("hasRole('Librarian')")
    @PostMapping(REST_MAPPING_CONSTRAINT.DEFINED_API.UPDATE_TRANSACTION)
    public ResponseEntity<BaseApiResponse> updateTransaction(@Valid @RequestBody TransactionRequest transactionRequest){
        try{
            BaseApiResponse baseApiResponse = transactionServiceImpl.updateTransaction(transactionRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body(baseApiResponse);
        } catch (Exception e) {
            BaseApiResponse response = new BaseApiResponse(INTERNAL_SERVER_ERROR,FAILURE, MESSAGE_COMMON_SERVER_ERROR, Collections.emptyList());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response); // Return 500 Internal Server Error
        }
    }

    @PreAuthorize("hasRole('Librarian')")
    @PostMapping(REST_MAPPING_CONSTRAINT.DEFINED_API.GET_TRANSACTION)
    public ResponseEntity<BaseApiResponse> getTransactionById(@Valid @RequestBody SearchRequest searchRequest){
        try{
            Long transactionId = searchRequest.getId();
            boolean findAll = searchRequest.isFindAll();
            BaseApiResponse transactionOptional = transactionServiceImpl.getTransactionByTransactionId(transactionId,findAll);

            if (transactionOptional.getSuccess()==1) {
                return ResponseEntity.ok(transactionOptional); // Return 200 OK with BaseApiResponse
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(transactionOptional); // Return 404 Not Found
            }
        } catch (Exception e) {
            BaseApiResponse response = new BaseApiResponse(INTERNAL_SERVER_ERROR,FAILURE, MESSAGE_COMMON_SERVER_ERROR, Collections.emptyList());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response); // Return 500 Internal Server Error
        }
    }
}

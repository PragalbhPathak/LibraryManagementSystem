package com.example.libraryManagementSystem.restController;

import com.example.libraryManagementSystem.constraint.REST_MAPPING_CONSTRAINT;
import com.example.libraryManagementSystem.dto.requestDto.BookRequest;
import com.example.libraryManagementSystem.dto.requestDto.SearchRequest;
import com.example.libraryManagementSystem.dto.responseDto.BaseApiResponse;
import com.example.libraryManagementSystem.implementation.BookServiceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;

import static com.example.libraryManagementSystem.constraint.REST_MAPPING_CONSTRAINT.BASE_URL;
import static com.example.libraryManagementSystem.constraint.REST_MAPPING_CONSTRAINT.COMMON_MESSAGE.*;
import static com.example.libraryManagementSystem.constraint.REST_MAPPING_CONSTRAINT.STATUS_CODES.*;
import static com.example.libraryManagementSystem.constraint.REST_MAPPING_CONSTRAINT.SUCCESS_STATUS.*;

@RestController
@RequestMapping(BASE_URL)
public class BookController {

    @Autowired
    private BookServiceImpl bookServiceImpl;

    @PreAuthorize("hasRole('Librarian')")
    @PostMapping(REST_MAPPING_CONSTRAINT.DEFINED_API.UPDATE_BOOK)
    public ResponseEntity<BaseApiResponse> updateBook(@Valid @RequestBody BookRequest bookRequest){
        try{
            BaseApiResponse baseApiResponse = bookServiceImpl.updateBook(bookRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body(baseApiResponse);
        } catch (Exception e) {
            BaseApiResponse response = new BaseApiResponse(INTERNAL_SERVER_ERROR,FAILURE, MESSAGE_COMMON_SERVER_ERROR, Collections.emptyList());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response); // Return 500 Internal Server Error
        }
    }

    @PreAuthorize("hasAnyRole('Librarian', 'Student')")
    @PostMapping(REST_MAPPING_CONSTRAINT.DEFINED_API.GET_BOOK)
    public ResponseEntity<BaseApiResponse> getBookById(@Valid @RequestBody SearchRequest searchRequest){
        try{
            Long bookId = searchRequest.getId();
            boolean findAll = searchRequest.isFindAll(); // Get findAll flag from request
            BaseApiResponse bookOptional = bookServiceImpl.getBookByBookId(bookId, findAll);

            if (bookOptional.getSuccess() == 1) {
                return ResponseEntity.ok(bookOptional); // Return 200 OK with BaseApiResponse
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(bookOptional); // Return 404 Not Found
            }
        } catch (Exception e) {
            BaseApiResponse response = new BaseApiResponse(INTERNAL_SERVER_ERROR, FAILURE, MESSAGE_COMMON_SERVER_ERROR, Collections.emptyList());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response); // Return 500 Internal Server Error
        }
    }

}

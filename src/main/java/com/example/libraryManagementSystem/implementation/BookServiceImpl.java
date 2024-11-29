package com.example.libraryManagementSystem.implementation;

import com.example.libraryManagementSystem.dto.requestDto.BookRequest;
import com.example.libraryManagementSystem.dto.responseDto.BaseApiResponse;

public interface BookServiceImpl {
    BaseApiResponse updateBook(BookRequest bookRequest);
    BaseApiResponse getBookByBookId(Long bookId, boolean findAll);
}

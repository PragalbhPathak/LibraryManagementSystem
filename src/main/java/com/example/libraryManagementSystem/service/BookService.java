package com.example.libraryManagementSystem.service;

import com.example.libraryManagementSystem.dto.requestDto.BookRequest;
import com.example.libraryManagementSystem.dto.responseDto.BaseApiResponse;
import com.example.libraryManagementSystem.dto.responseDto.BookResponse;
import com.example.libraryManagementSystem.entity.Book;
import com.example.libraryManagementSystem.implementation.BookServiceImpl;
import com.example.libraryManagementSystem.repository.BookRepository;
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
public class BookService implements BookServiceImpl {
    @Autowired
    private BookRepository bookRepository;

    @Override
    @Transactional
    public BaseApiResponse updateBook(BookRequest bookRequest) {
        Book book = new Book();

        try {
            if (bookRequest.getTitle() == null || bookRequest.getTitle().isEmpty()) {
                return new BaseApiResponse(BAD_REQUEST, FAILURE, "Title is required", Collections.emptyList());
            }
            if (bookRequest.getAuthor() == null || bookRequest.getAuthor().isEmpty()) {
                return new BaseApiResponse(BAD_REQUEST, FAILURE, "Author is required", Collections.emptyList());
            }
            if (bookRequest.getPublicationYear() <= 0 || bookRequest.getPublicationYear() > LocalDate.now().getYear()) {
                return new BaseApiResponse(BAD_REQUEST, FAILURE, "Invalid publication year", Collections.emptyList());
            }
            if (bookRequest.getCategory() == null || bookRequest.getCategory().isEmpty()) {
                return new BaseApiResponse(BAD_REQUEST, FAILURE, "Category is required", Collections.emptyList());
            }

            if (bookRequest.getBookId() == null || bookRequest.getBookId() <= 0) {
                // If bookId is null or 0, create a new book
                book.setTitle(bookRequest.getTitle());
                book.setAuthor(bookRequest.getAuthor());
                book.setPublicationYear(bookRequest.getPublicationYear());
                book.setCategory(bookRequest.getCategory());
                book.setAvailabilityStatus(bookRequest.isAvailabilityStatus());

                book = bookRepository.save(book);

                BookResponse bookResponse = mapToBookResponse(book);

                return new BaseApiResponse(SUCCESS_OK, SUCCESS, MESSAGE_SAVE, bookResponse);
            } else {
                // If bookId is provided, check if the book exists
                Optional<Book> existingBook = bookRepository.findById(bookRequest.getBookId());

                if (existingBook.isPresent()) {
                    // Update the existing book
                    book = existingBook.get();
                    book.setTitle(bookRequest.getTitle());
                    book.setAuthor(bookRequest.getAuthor());
                    book.setPublicationYear(bookRequest.getPublicationYear());
                    book.setCategory(bookRequest.getCategory());
                    book.setAvailabilityStatus(bookRequest.isAvailabilityStatus());

                    book = bookRepository.save(book);

                    BookResponse bookResponse = mapToBookResponse(book);

                    return new BaseApiResponse(SUCCESS_OK, SUCCESS, MESSAGE_SAVE, bookResponse);
                } else {
                    // If book doesn't exist, return an error
                    return new BaseApiResponse(BAD_REQUEST, FAILURE, "Book with ID " + bookRequest.getBookId() + " not found", Collections.emptyList());
                }
            }

        } catch (Exception e) {
            return new BaseApiResponse(INTERNAL_SERVER_ERROR, FAILURE, MESSAGE_COMMON_SERVER_ERROR, Collections.emptyList());
        }
    }

    // Helper method to map Book to BookResponseDto
    private BookResponse mapToBookResponse(Book book) {
        BookResponse bookResponse = new BookResponse();
        bookResponse.setBookId(book.getBookId());
        bookResponse.setTitle(book.getTitle());
        bookResponse.setAuthor(book.getAuthor());
        bookResponse.setPublicationYear(book.getPublicationYear());
        bookResponse.setCategory(book.getCategory());
        bookResponse.setAvailabilityStatus(book.isAvailabilityStatus());
        return bookResponse;
    }

    // New method to handle fetching all books
    private BaseApiResponse getAllBooks() {
        try {
            List<Book> books = bookRepository.findAll();
            List<BookResponse> bookResponses = books.stream()
                    .map(this::mapToBookResponse)
                    .collect(Collectors.toList());
            return new BaseApiResponse(SUCCESS_OK, SUCCESS, MESSAGE_FETCH_DATA, bookResponses);
        } catch (Exception e) {
            return new BaseApiResponse(INTERNAL_SERVER_ERROR, FAILURE, MESSAGE_COMMON_SERVER_ERROR, Collections.emptyList());
        }
    }

    public BaseApiResponse getBookByBookId(Long bookId, boolean findAll) {
        try {
            if (findAll) {
                return getAllBooks();
            } else {
                Optional<Book> bookOptional = bookRepository.findById(bookId);

                if (bookOptional.isPresent()) {
                    Book book = bookOptional.get();
                    BookResponse bookResponse = mapToBookResponse(book);
                    return new BaseApiResponse(SUCCESS_OK, SUCCESS, MESSAGE_FETCH_DATA, bookResponse);
                } else {
                    return new BaseApiResponse(NOT_FOUND, FAILURE, MESSAGE_NOT_FOUND, Collections.emptyList());
                }
            }
        } catch (Exception e) {
            return new BaseApiResponse(INTERNAL_SERVER_ERROR, FAILURE, MESSAGE_COMMON_SERVER_ERROR, Collections.emptyList());
        }
    }

}

//-----------------------------------------------------------------------------------------------------------------------

//    @Transactional
//    public BaseApiResponse updateBook(BookRequest bookRequest) {
//        Book book = new Book();
//
//        try {
//            // Validate userId, allow null or 0 for new user creation
//            if (bookRequest.getBookId() != null && bookRequest.getBookId() <= 0) {
//                return new BaseApiResponse(BAD_REQUEST, FAILURE, "Invalid User ID", Collections.emptyList());
//            }
//
//            // Validate Title
//            if (bookRequest.getTitle() == null || bookRequest.getTitle().isEmpty()) {
//                return new BaseApiResponse(BAD_REQUEST, FAILURE, "Title is required", Collections.emptyList());
//            }
//
//            // Validate Author
//            if (bookRequest.getAuthor() == null || bookRequest.getAuthor().isEmpty()) {
//                return new BaseApiResponse(BAD_REQUEST, FAILURE, "Author is required", Collections.emptyList());
//            }
//
//            // Validate Publication Year (it should be a reasonable number, e.g., not in the future)
//            if (bookRequest.getPublicationYear() <= 0 || bookRequest.getPublicationYear() > LocalDate.now().getYear()) {
//                return new BaseApiResponse(BAD_REQUEST, FAILURE, "Invalid publication year", Collections.emptyList());
//            }
//
//            // Validate Category
//            if (bookRequest.getCategory() == null || bookRequest.getCategory().isEmpty()) {
//                return new BaseApiResponse(BAD_REQUEST, FAILURE, "Category is required", Collections.emptyList());
//            }
//            Optional<Book> existingBook = bookRepository.findById(bookRequest.getBookId());
//
//            if (existingBook.isPresent()) {
//                book = existingBook.get();
//                book.setAvailabilityStatus(bookRequest.isAvailabilityStatus());
//                book.setPublicationYear(bookRequest.getPublicationYear());
//
//                book = bookRepository.save(book);
//            } else {
//                book.setBookId(bookRequest.getBookId());
//                book.setTitle(bookRequest.getTitle());
//                book.setAuthor(bookRequest.getAuthor());
//                book.setPublicationYear(bookRequest.getPublicationYear());
//                book.setCategory(bookRequest.getCategory());
//                book.setAvailabilityStatus(bookRequest.isAvailabilityStatus());
//
//                book = bookRepository.save(book);
//            }
//
//            BookResponse bookResponse = mapToBookResponse(book);
//
//            return new BaseApiResponse(SUCCESS_OK, SUCCESS, MESSAGE_SAVE, bookResponse);
//        } catch (Exception e) {
//            return new BaseApiResponse(INTERNAL_SERVER_ERROR, FAILURE, MESSAGE_COMMON_SERVER_ERROR, Collections.emptyList());
//        }
//    }
//
//    // Helper method to map User to UserResponseDto
//    private BookResponse mapToBookResponse(Book book) {
//        BookResponse bookResponse = new BookResponse();
//        bookResponse.setBookId(book.getBookId());
//        bookResponse.setTitle(book.getTitle());
//        bookResponse.setAuthor(book.getAuthor());
//        bookResponse.setPublicationYear(book.getPublicationYear());
//        bookResponse.setCategory(book.getCategory());
//        bookResponse.setAvailabilityStatus(book.isAvailabilityStatus());
//        return bookResponse;
//    }









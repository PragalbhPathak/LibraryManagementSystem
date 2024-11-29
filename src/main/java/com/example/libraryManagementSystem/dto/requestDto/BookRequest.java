package com.example.libraryManagementSystem.dto.requestDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookRequest {
    private Long bookId;
    private String title;
    private String author;
    private int publicationYear;
    private String category;
    private boolean availabilityStatus;
}


//package com.example.libraryManagementSystem.dto.requestDto;
//
//import lombok.AllArgsConstructor;
//import lombok.Data;
//import lombok.NoArgsConstructor;
//
//@Data
//@NoArgsConstructor
//@AllArgsConstructor
//public class BookRequest {
//    private Long bookId;
//    private String title;
//    private String author;
//    private int publicationYear;
//    private String category;
//    private boolean availabilityStatus;
//
//    // New field to handle fetching all books
//    private boolean findAll = true; // Default value is true
//}

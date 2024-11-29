package com.example.libraryManagementSystem.dto.responseDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BaseApiResponse {
    private String status;
    private int success;
    private String message;
    private Object data;
}

package com.example.libraryManagementSystem.dto.requestDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRequest {
    private Long userId;
    private String name;
    private String email;
    private String password;
    private String contact;
    private String address;
    private String role;
}

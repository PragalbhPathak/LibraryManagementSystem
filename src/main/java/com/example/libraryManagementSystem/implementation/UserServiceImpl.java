package com.example.libraryManagementSystem.implementation;

import com.example.libraryManagementSystem.dto.requestDto.AuthRequest;
import com.example.libraryManagementSystem.dto.requestDto.UserRequest;
import com.example.libraryManagementSystem.dto.responseDto.BaseApiResponse;

public interface UserServiceImpl {
    BaseApiResponse createUser(UserRequest userRequest);
    BaseApiResponse getUserByUserId(Long userId,boolean findAll);
    BaseApiResponse userLogin(AuthRequest authRequest);

}

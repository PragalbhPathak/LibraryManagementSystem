package com.example.libraryManagementSystem.implementation;

import com.example.libraryManagementSystem.dto.requestDto.FineRequest;
import com.example.libraryManagementSystem.dto.responseDto.BaseApiResponse;

public interface FineServiceImpl {
    BaseApiResponse updateFine(FineRequest fineRequest);
    BaseApiResponse getFineByFineId(Long fineId,boolean findAll);
}

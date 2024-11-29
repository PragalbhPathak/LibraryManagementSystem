package com.example.libraryManagementSystem.service;

import com.example.libraryManagementSystem.dto.requestDto.FineRequest;
import com.example.libraryManagementSystem.dto.responseDto.BaseApiResponse;
import com.example.libraryManagementSystem.dto.responseDto.FineResponse;
import com.example.libraryManagementSystem.entity.Fine;
import com.example.libraryManagementSystem.implementation.FineServiceImpl;
import com.example.libraryManagementSystem.repository.FineRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.example.libraryManagementSystem.constraint.REST_MAPPING_CONSTRAINT.COMMON_MESSAGE.*;
import static com.example.libraryManagementSystem.constraint.REST_MAPPING_CONSTRAINT.STATUS_CODES.*;
import static com.example.libraryManagementSystem.constraint.REST_MAPPING_CONSTRAINT.SUCCESS_STATUS.FAILURE;
import static com.example.libraryManagementSystem.constraint.REST_MAPPING_CONSTRAINT.SUCCESS_STATUS.SUCCESS;

@Service
public class FineService implements FineServiceImpl {

    @Autowired
    private FineRepository fineRepository;

    @Transactional
    public BaseApiResponse updateFine(FineRequest fineRequest) {
        Fine fine = new Fine();
        try {
            if (fineRequest.getTransactionId() == null || fineRequest.getTransactionId() == 0) {
                return new BaseApiResponse(BAD_REQUEST, FAILURE, "Transaction Id is required", Collections.emptyList());
            }
            if (fineRequest.getAmount() == null) {
                return new BaseApiResponse(BAD_REQUEST, FAILURE, "Amount may be 0 but not null", Collections.emptyList());
            }
            if (fineRequest.getFineId() == null || fineRequest.getFineId() <= 0) {
                // If fineId is null or 0, create a new fine
                fine.setFineId(fineRequest.getFineId());
                fine.setTransactionId(fineRequest.getTransactionId());
                fine.setAmount(fineRequest.getAmount());
                fine.setPaid(fineRequest.isPaid());

                fine = fineRepository.save(fine);
                FineResponse fineResponse = mapToFineResponse(fine);

                return new BaseApiResponse(SUCCESS_OK, SUCCESS, MESSAGE_SAVE, fineResponse);
            } else {
                // If fineId is provided, check if the book exists
                Optional<Fine> existingFine = fineRepository.findById(fineRequest.getFineId());

                if (existingFine.isPresent()) {
                    // Update the existing fine
                    fine = existingFine.get();
                    fine.setTransactionId(fineRequest.getTransactionId());
                    fine.setAmount(fineRequest.getAmount());
                    fine.setPaid(fineRequest.isPaid());

                    fine = fineRepository.save(fine);
                    FineResponse fineResponse = mapToFineResponse(fine);

                    return new BaseApiResponse(SUCCESS_OK, SUCCESS, MESSAGE_SAVE, fineResponse);
                } else {
                    // If book doesn't exist, return an error
                    return new BaseApiResponse(BAD_REQUEST, FAILURE, "Fine with ID " + fineRequest.getFineId() + " not found", Collections.emptyList());
                }
            }
        } catch (Exception e) {
            return new BaseApiResponse(INTERNAL_SERVER_ERROR, FAILURE, MESSAGE_COMMON_SERVER_ERROR, Collections.emptyList());
        }
    }

    // Helper method to map User to UserResponseDto
    private FineResponse mapToFineResponse(Fine fine) {
        FineResponse fineResponse = new FineResponse();
        fineResponse.setFineId(fine.getFineId());
        fineResponse.setTransactionId(fine.getTransactionId());
        fineResponse.setAmount(fine.getAmount());
        fineResponse.setPaid(fine.isPaid());

        return fineResponse;
    }

    // New method to handle fetching all fines
    private BaseApiResponse getAllFine() {
        try {
            List<Fine> fines = fineRepository.findAll();
            List<FineResponse> fineResponses = fines.stream()
                    .map(this::mapToFineResponse)
                    .collect(Collectors.toList());
            return new BaseApiResponse(SUCCESS_OK, SUCCESS, MESSAGE_FETCH_DATA, fineResponses);
        } catch (Exception e) {
            return new BaseApiResponse(INTERNAL_SERVER_ERROR, FAILURE, MESSAGE_COMMON_SERVER_ERROR, Collections.emptyList());
        }
    }

    public BaseApiResponse getFineByFineId(Long fineId, boolean findAll) {
        try {
            if (findAll) {
                return getAllFine();
            } else {
                Optional<Fine> fineOptional = fineRepository.findById(fineId);

                if (fineOptional.isPresent()) {
                    Fine fine = fineOptional.get();
                    FineResponse fineResponse = mapToFineResponse(fine);
                    return new BaseApiResponse(SUCCESS_OK, SUCCESS, MESSAGE_FETCH_DATA, fineResponse);
                } else {
                    return new BaseApiResponse(NOT_FOUND, FAILURE, MESSAGE_NOT_FOUND, Collections.emptyList());
                }
            }
        } catch (Exception e) {
            return new BaseApiResponse(INTERNAL_SERVER_ERROR, FAILURE, MESSAGE_COMMON_SERVER_ERROR, Collections.emptyList());
        }
    }
}

//---------------------------------------------------------------------------------------------------------------------------

//    @Transactional
//    public BaseApiResponse updateFine(FineRequest fineRequest) {
//        Fine fine = new Fine();
//
//        try {
//            Optional<Fine> existingFine = fineRepository.findById(fineRequest.getFineId());
//
//            if (existingFine.isPresent()) {
//                fine = existingFine.get();
//                fine.setTransactionId(fineRequest.getTransactionId());
//                fine.setAmount(fineRequest.getAmount());
//                fine.setPaid(fineRequest.isPaid());
//
//                fine = fineRepository.save(fine);
//            } else {
//                fine.setFineId(fineRequest.getFineId());
//                fine.setTransactionId(fineRequest.getTransactionId());
//                fine.setAmount(fineRequest.getAmount());
//                fine.setPaid(fineRequest.isPaid());
//
//                fine = fineRepository.save(fine);
//            }
//
//            FineResponse fineResponse = mapToFineResponse(fine);
//
//            return new BaseApiResponse(SUCCESS_OK, SUCCESS, MESSAGE_SAVE, fineResponse);
//        } catch (Exception e) {
//            return new BaseApiResponse(INTERNAL_SERVER_ERROR, FAILURE, MESSAGE_COMMON_SERVER_ERROR, Collections.emptyList());
//        }
//    }
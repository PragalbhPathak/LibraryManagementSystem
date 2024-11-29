package com.example.libraryManagementSystem.restController;

import com.example.libraryManagementSystem.constraint.REST_MAPPING_CONSTRAINT;
import com.example.libraryManagementSystem.dto.requestDto.FineRequest;
import com.example.libraryManagementSystem.dto.requestDto.SearchRequest;
import com.example.libraryManagementSystem.dto.responseDto.BaseApiResponse;
import com.example.libraryManagementSystem.implementation.FineServiceImpl;
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
import static com.example.libraryManagementSystem.constraint.REST_MAPPING_CONSTRAINT.SUCCESS_STATUS.FAILURE;

@RestController
@RequestMapping(BASE_URL)
public class FineController {

    @Autowired
    private FineServiceImpl fineServiceImpl;

    @PreAuthorize("hasRole('Librarian')")
    @PostMapping(REST_MAPPING_CONSTRAINT.DEFINED_API.UPDATE_FINE)
    public ResponseEntity<BaseApiResponse> updateFine(@Valid  @RequestBody FineRequest fineRequest){
        try{
            BaseApiResponse baseApiResponse = fineServiceImpl.updateFine(fineRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body(baseApiResponse);
        } catch (Exception e) {
            BaseApiResponse response = new BaseApiResponse(INTERNAL_SERVER_ERROR,FAILURE, MESSAGE_COMMON_SERVER_ERROR, Collections.emptyList());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PreAuthorize("hasRole('Librarian')")
    @PostMapping(REST_MAPPING_CONSTRAINT.DEFINED_API.GET_FINE)
    public ResponseEntity<BaseApiResponse> getFineById(@Valid @RequestBody SearchRequest searchRequest){
        try{
            Long fineId = searchRequest.getId();
            boolean findAll = searchRequest.isFindAll();
            BaseApiResponse fineOptional = fineServiceImpl.getFineByFineId(fineId,findAll);

            if (fineOptional.getSuccess()==1){
                return ResponseEntity.ok(fineOptional);
            }else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(fineOptional);
            }
        } catch (Exception e) {
            BaseApiResponse response = new BaseApiResponse(INTERNAL_SERVER_ERROR,FAILURE, MESSAGE_COMMON_SERVER_ERROR, Collections.emptyList());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}

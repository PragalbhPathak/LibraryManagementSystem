package com.example.libraryManagementSystem.restController;

import com.example.libraryManagementSystem.constraint.REST_MAPPING_CONSTRAINT;
import com.example.libraryManagementSystem.dto.requestDto.AuthRequest;
import com.example.libraryManagementSystem.dto.requestDto.Checksum;
import com.example.libraryManagementSystem.dto.requestDto.SearchRequest;
import com.example.libraryManagementSystem.dto.requestDto.UserRequest;
import com.example.libraryManagementSystem.dto.responseDto.BaseApiResponse;
import com.example.libraryManagementSystem.entity.User;
import com.example.libraryManagementSystem.implementation.UserServiceImpl;
import com.example.libraryManagementSystem.encryption.ChecksumEncryption;
import com.example.libraryManagementSystem.repository.UserRepository;
import com.example.libraryManagementSystem.service.JwtService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;

import static com.example.libraryManagementSystem.constraint.REST_MAPPING_CONSTRAINT.BASE_URL;
import static com.example.libraryManagementSystem.constraint.REST_MAPPING_CONSTRAINT.COMMON_MESSAGE.*;
import static com.example.libraryManagementSystem.constraint.REST_MAPPING_CONSTRAINT.STATUS_CODES.*;
import static com.example.libraryManagementSystem.constraint.REST_MAPPING_CONSTRAINT.SUCCESS_STATUS.*;

@RestController
@RequestMapping(BASE_URL)
public class UserController {

    @Autowired(required = true)
    private UserServiceImpl userServiceImpl;



    @PreAuthorize("hasRole('Librarian')")
    @PostMapping(REST_MAPPING_CONSTRAINT.DEFINED_API.REGISTER_USER)
    public ResponseEntity<BaseApiResponse> createUser(@RequestBody Checksum checksum) throws Exception{
        ObjectMapper objectMapper = new ObjectMapper();
        String decryption = ChecksumEncryption.decryptText(checksum.getChecksum());
        UserRequest userRequest = objectMapper.readValue(decryption, UserRequest.class);

        // Validate the UserRequest object
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            Validator validator = factory.getValidator();
            Set<ConstraintViolation<UserRequest>> violations = validator.validate(userRequest);
            if (!violations.isEmpty()) {
                StringBuilder errorMessages = new StringBuilder();
                for (ConstraintViolation<UserRequest> violation : violations) {
                    errorMessages.append(violation.getMessage()).append("; ");
                }
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new BaseApiResponse(BAD_REQUEST, FAILURE, errorMessages.toString(), Collections.emptyList()));
            }
        }
        try{
            BaseApiResponse baseApiResponse = userServiceImpl.createUser(userRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body(baseApiResponse);
        } catch (Exception e) {
            BaseApiResponse response = new BaseApiResponse(INTERNAL_SERVER_ERROR,FAILURE, MESSAGE_COMMON_SERVER_ERROR, Collections.emptyList());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response); // Return 500 Internal Server Error
        }
    }

//    @PreAuthorize("hasRole('Librarian')")
//    @PostMapping(REST_MAPPING_CONSTRAINT.DEFINED_API.GET_USER)
//    public ResponseEntity<BaseApiResponse> getUserById(@RequestBody SearchRequest searchRequest) {
//        try {
//            Long userId = searchRequest.getId();
//            boolean findAll = searchRequest.isFindAll();
//
//            BaseApiResponse response = userServiceImpl.getUserByUserId(userId, findAll);
//
//            if (response.getSuccess() == 1) {
//                return ResponseEntity.ok(response);
//            } else {
//                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
//            }
//        } catch (Exception e) {
//            BaseApiResponse response = new BaseApiResponse(INTERNAL_SERVER_ERROR, FAILURE, MESSAGE_COMMON_SERVER_ERROR, Collections.emptyList());
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
//        }
//    }

    @PreAuthorize("hasRole('Librarian')")
    @PostMapping(REST_MAPPING_CONSTRAINT.DEFINED_API.GET_USER)
    public ResponseEntity<BaseApiResponse> getUserById(@Valid @RequestBody SearchRequest searchRequest) {
        try {
            // Pass `findAll` and `id` to service layer
            Long userId = searchRequest.getId();
            boolean findAll = searchRequest.isFindAll();

            BaseApiResponse userOptional = userServiceImpl.getUserByUserId(userId, findAll);

            if (userOptional.getSuccess() == 1) {
                // If users are found, return success response with the data
                return ResponseEntity.ok(userOptional);  // Return 200 OK with BaseApiResponse
            } else {
                // If no users are found or there's an issue, return failure response with appropriate message
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(userOptional); // Return 404 Not Found
            }
        } catch (Exception e) {
            // Handle any unexpected errors (e.g., database errors)
            BaseApiResponse response = new BaseApiResponse(INTERNAL_SERVER_ERROR, FAILURE, MESSAGE_COMMON_SERVER_ERROR, Collections.emptyList());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response); // Return 500 Internal Server Error
        }
    }
    //-----------------------------------------------------------------------------------------------------------------------------------------
                                                //LOGIN USER
    //-----------------------------------------------------------------------------------------------------------------------------------------

//    @PostMapping(REST_MAPPING_CONSTRAINT.DEFINED_API.LOGIN_USER)
//    public ResponseEntity<BaseApiResponse> userLogin(@RequestBody AuthRequest authRequest) {
//        try {
//            if (authRequest.getEmail() == null || authRequest.getPassword() == null) {
//                // Return bad request if email or password is missing
//                BaseApiResponse errorResponse = new BaseApiResponse(BAD_REQUEST,FAILURE, "Email and password are required", Collections.emptyList());
//                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
//            }
//            // Use the service method to handle the login
//            BaseApiResponse userOptional = userServiceImpl.userLogin(authRequest);
//
//            // Check the response and return appropriate status
//            if (userOptional.getSuccess() == 1) {
//                BaseApiResponse response = new BaseApiResponse(SUCCESS_OK,SUCCESS, MESSAGE_FETCH_DATA, userOptional);
//                return ResponseEntity.ok(response);  // Successful login
//            } else {
//                BaseApiResponse response = new BaseApiResponse(NOT_FOUND,FAILURE, MESSAGE_NOT_FOUND, Collections.emptyList());
//                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response); // Unauthorized
//            }
//        } catch (Exception e) {
//            // Handle exception and return server error response
//            BaseApiResponse response = new BaseApiResponse(UNAUTHORIZED, FAILURE, "Login failed", Collections.emptyList());
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
//        }
//    }


    // Endpoint for user login
    @PostMapping(REST_MAPPING_CONSTRAINT.DEFINED_API.LOGIN_USER)
    public ResponseEntity<BaseApiResponse> userLogin(@RequestBody AuthRequest authRequest) {
        try {
            // Call the service method to handle the login logic
            BaseApiResponse response = userServiceImpl.userLogin(authRequest);

            // Check if login is successful
            if (response.getSuccess() == 1) {
                // Successful login: Return token and success message
                return ResponseEntity.ok(response);
            } else {
                // Failure: Role mismatch or invalid credentials
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
        } catch (Exception e) {
            // In case of any unexpected error, return server error response
            BaseApiResponse errorResponse = new BaseApiResponse(UNAUTHORIZED, FAILURE, "Login failed: " , Collections.emptyList());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

//    @PostMapping(REST_MAPPING_CONSTRAINT.DEFINED_API.LOGIN_USER)
//    public ResponseEntity<BaseApiResponse> userLogin(@RequestBody AuthRequest authRequest) {
//        try {
//            // Check if email or password is missing or invalid
//            if (authRequest.getEmail() == null || authRequest.getPassword() == null) {
//                BaseApiResponse errorResponse = new BaseApiResponse(BAD_REQUEST, FAILURE, "Email and password are required", Collections.emptyList());
//                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
//            }
//
//            // Check if email format is valid
//            if (!isValidEmail(authRequest.getEmail())) {
//                BaseApiResponse errorResponse = new BaseApiResponse(BAD_REQUEST, FAILURE, "Invalid email format", Collections.emptyList());
//                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
//            }
//
//            // Check for blank email or password
//            if (authRequest.getEmail().trim().isEmpty() || authRequest.getPassword().trim().isEmpty()) {
//                BaseApiResponse errorResponse = new BaseApiResponse(BAD_REQUEST, FAILURE, "Email and password cannot be empty or just spaces", Collections.emptyList());
//                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
//            }
//
//            // Use the service method to handle the login
//            BaseApiResponse userOptional = userServiceImpl.userLogin(authRequest);
//
//            // Check the response and return appropriate status
//            if (userOptional.getSuccess() == 1) {
//                BaseApiResponse response = new BaseApiResponse(SUCCESS_OK, SUCCESS, "Login successful", userOptional);
//                return ResponseEntity.ok(response);  // Successful login
//            } else {
//                BaseApiResponse response = new BaseApiResponse(NOT_FOUND, FAILURE, "User not found", Collections.emptyList());
//                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response); // Unauthorized
//            }
//        } catch (Exception e) {
//            // Handle exception and return server error response
//            BaseApiResponse response = new BaseApiResponse(UNAUTHORIZED, FAILURE, "Login failed", Collections.emptyList());
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
//        }
//    }
//
//    // Helper method for email format validation using regex
//    private boolean isValidEmail(String email) {
//        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
//        return email != null && email.matches(emailRegex);
//    }

}

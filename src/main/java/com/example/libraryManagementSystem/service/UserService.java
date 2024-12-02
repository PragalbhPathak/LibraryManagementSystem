package com.example.libraryManagementSystem.service;

import com.example.libraryManagementSystem.dto.requestDto.AuthRequest;
import com.example.libraryManagementSystem.dto.requestDto.UserRequest;
import com.example.libraryManagementSystem.dto.responseDto.BaseApiResponse;
import com.example.libraryManagementSystem.dto.responseDto.UserResponse;
import com.example.libraryManagementSystem.entity.User;
import com.example.libraryManagementSystem.implementation.UserServiceImpl;
import com.example.libraryManagementSystem.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.example.libraryManagementSystem.constraint.REST_MAPPING_CONSTRAINT.COMMON_MESSAGE.*;
import static com.example.libraryManagementSystem.constraint.REST_MAPPING_CONSTRAINT.STATUS_CODES.*;
import static com.example.libraryManagementSystem.constraint.REST_MAPPING_CONSTRAINT.SUCCESS_STATUS.FAILURE;
import static com.example.libraryManagementSystem.constraint.REST_MAPPING_CONSTRAINT.SUCCESS_STATUS.SUCCESS;

@Service
public class UserService implements UserServiceImpl {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtService jwtService;

    @Transactional
    public BaseApiResponse createUser(UserRequest userRequest) {
        User user = new User();
        try {
            // Validate userId, allow null or 0 for new user creation
            if (userRequest.getUserId() != null && userRequest.getUserId() <= 0) {
                return new BaseApiResponse(BAD_REQUEST, FAILURE, "Invalid User ID", Collections.emptyList());
            }
            // Validate other fields
            if (userRequest.getName() == null || userRequest.getName().isEmpty()) {
                return new BaseApiResponse(BAD_REQUEST, FAILURE, "Name is required", Collections.emptyList());
            }
            if (userRequest.getEmail() == null || userRequest.getEmail().isEmpty() || !userRequest.getEmail().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
                return new BaseApiResponse(BAD_REQUEST, FAILURE, "Valid email is required", Collections.emptyList());
            }
            // Check if the email already exists for other users
            Optional<User> existingUserByEmail = userRepository.findByEmail(userRequest.getEmail());
            if (existingUserByEmail.isPresent()) {
                if (userRequest.getUserId() == null || !userRequest.getUserId().equals(existingUserByEmail.get().getUserId())) {
                    return new BaseApiResponse(BAD_REQUEST, FAILURE, "Email is already in use by another user", Collections.emptyList());
                }
            }
            if (userRequest.getPassword() == null || userRequest.getPassword().isEmpty() || userRequest.getPassword().length() < 6) {
                return new BaseApiResponse(BAD_REQUEST, FAILURE, "Password must be required, at least 6 characters long", Collections.emptyList());
            }
            // Validate contact
            if (userRequest.getContact() == null || userRequest.getContact().isEmpty() || !userRequest.getContact().matches("^[0-9]{10,12}$")) {
                return new BaseApiResponse(BAD_REQUEST, FAILURE, "Contact is required, within 10 - 12 digits ", Collections.emptyList());
            }
            // Validate address
            if (userRequest.getAddress() == null || userRequest.getAddress().isEmpty()) {
                return new BaseApiResponse(BAD_REQUEST, FAILURE, "Address is required", Collections.emptyList());
            }
            // Validate role
            if (userRequest.getRole() == null || userRequest.getRole().isEmpty()) {
                return new BaseApiResponse(BAD_REQUEST, FAILURE, "Role is required", Collections.emptyList());
            }
            if (!userRequest.getRole().equalsIgnoreCase("Librarian") && !userRequest.getRole().equalsIgnoreCase("Student")) {
                return new BaseApiResponse(BAD_REQUEST, FAILURE, "Invalid role, allowed roles are 'Librarian' or 'Student'", Collections.emptyList());
            }
            // If the userId is null or 0, create a new user
            if (userRequest.getUserId() == null || userRequest.getUserId() <= 0) {
                // New user, don't check for existing user ID
                user.setName(userRequest.getName());
                user.setEmail(userRequest.getEmail());
                user.setPassword(passwordEncoder.encode(userRequest.getPassword()));
                user.setContact(userRequest.getContact());
                user.setAddress(userRequest.getAddress());
                user.setRole(userRequest.getRole());
                // Save the new user
                user = userRepository.save(user);
            } else {
                // If userId is not null, check if the user exists for update
                Optional<User> existingUser = userRepository.findById(userRequest.getUserId());

                if (existingUser.isPresent()) {
                    // Update existing user details
                    user = existingUser.get();
                    user.setPassword(passwordEncoder.encode(userRequest.getPassword()));
                    user.setContact(userRequest.getContact());
                    user.setAddress(userRequest.getAddress());
                    user.setRole(userRequest.getRole());
                    // Save the updated user
                    user = userRepository.save(user);
                } else {
                    // If user does not exist, return error
                    return new BaseApiResponse(BAD_REQUEST, FAILURE, "User with ID " + userRequest.getUserId() + " not found", Collections.emptyList());
                }
            }
            // Map the user entity to UserResponseDto
            UserResponse userResponse = mapToUserResponse(user);
            return new BaseApiResponse(SUCCESS_OK, SUCCESS, MESSAGE_SAVE, userResponse);
        } catch (Exception e) {
            // Handle unexpected exceptions
            return new BaseApiResponse(INTERNAL_SERVER_ERROR, FAILURE, MESSAGE_COMMON_SERVER_ERROR, Collections.emptyList());
        }
    }

    // Helper method to map User to UserResponseDto
    private UserResponse mapToUserResponse(User user) {
        UserResponse userResponse = new UserResponse();
        userResponse.setUserId(user.getUserId());
        userResponse.setName(user.getName());
        userResponse.setEmail(user.getEmail());
        userResponse.setContact(user.getContact());
        userResponse.setAddress(user.getAddress());
        userResponse.setRole(user.getRole());
        return userResponse;
    }

    private BaseApiResponse getAllUsers(Pageable pageable) {
        try {
            // Using Page object to fetch paginated users
            Page<User> users = userRepository.findAll(pageable);
            if (users.hasContent()) {
                // Map the users to the UserResponse DTO
                var userResponses = users.stream()
                        .map(this::mapToUserResponse)
                        .collect(Collectors.toList());
                return new BaseApiResponse(SUCCESS_OK, SUCCESS, MESSAGE_FETCH_DATA, userResponses);
            } else {
                return new BaseApiResponse(NOT_FOUND, FAILURE, MESSAGE_NOT_FOUND, Collections.emptyList());
            }
        } catch (Exception e) {
            return new BaseApiResponse(INTERNAL_SERVER_ERROR, FAILURE, MESSAGE_COMMON_SERVER_ERROR, Collections.emptyList());
        }
    }

    @Override
    public BaseApiResponse getUserByUserId(Long userId, boolean findAll) {
        try {
            if (findAll) {
                // Fetch all users with pagination (limit 8)
                Pageable pageable = PageRequest.of(0, 8);  // Limit to 8 records per page
                return getAllUsers(pageable);
            } else {
                // Fetch a specific user by userId
                Optional<User> userOptional = userRepository.findById(userId);
                if (userOptional.isPresent()) {
                    User user = userOptional.get();
                    UserResponse userResponse = mapToUserResponse(user);
                    return new BaseApiResponse(SUCCESS_OK, SUCCESS, MESSAGE_FETCH_DATA, userResponse);
                } else {
                    return new BaseApiResponse(NOT_FOUND, FAILURE, MESSAGE_NOT_FOUND, Collections.emptyList());
                }
            }
        } catch (Exception e) {
            return new BaseApiResponse(INTERNAL_SERVER_ERROR, FAILURE, MESSAGE_COMMON_SERVER_ERROR, Collections.emptyList());
        }
    }

    @Override
    public BaseApiResponse userLogin(AuthRequest authRequest) {
        // Extracting email, password, and role from the AuthRequest object
        String email = authRequest.getEmail();
        String password = authRequest.getPassword();
        String requestedRole = authRequest.getRole();  // Role from the request

        // Email format validation
        if (!isValidEmail(email)) {
            return new BaseApiResponse(BAD_REQUEST, FAILURE, "Invalid email format", Collections.emptyList());
        }
        // Check if email or password is blank (contains only spaces)
        if (email.trim().isEmpty() || password.trim().isEmpty()) {
            return new BaseApiResponse(BAD_REQUEST, FAILURE, "Email and password cannot be empty or just spaces", Collections.emptyList());
        }

        // Fetching user from repository based on email
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            // Checking if password matches
            if (passwordEncoder.matches(password, user.getPassword())) {
                // Check if the requested role matches the user's role (case-insensitive)
                if (user.getRole().equalsIgnoreCase(requestedRole)) {
                    // If roles match, generate the JWT token
                    String token = jwtService.generateToken(user.getEmail(), user.getRole());
                    // Returning success response with token
                    return new BaseApiResponse(SUCCESS_OK, SUCCESS, "Login successful", token);
                } else {
                    // If the role doesn't match, return an unauthorized response
                    return new BaseApiResponse(BAD_REQUEST, FAILURE, "Role mismatch: Requested role does not match user role", Collections.emptyList());
                }
            } else {
                return new BaseApiResponse(BAD_REQUEST, FAILURE, "Invalid credentials: Incorrect password", Collections.emptyList());
            }
        } else {
            return new BaseApiResponse(NOT_FOUND, FAILURE, "User not found", Collections.emptyList());
        }
    }
    // Helper method for email format validation using regex
    private boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        return email != null && email.matches(emailRegex);
    }

}

//-------------------------------------------------------------------------------------------------------------------------

//    @Override
//    public BaseApiResponse userLogin(AuthRequest authRequest) {
//        // Extracting email, password, and role from the AuthRequest object
//        String email = authRequest.getEmail();
//        String password = authRequest.getPassword();
//        String requestedRole = authRequest.getRole();  // Role from the request
//
//        // Fetching user from repository based on email
//        Optional<User> userOptional = userRepository.findByEmail(email);
//
//        if (userOptional.isPresent()) {
//            User user = userOptional.get();
//
//            // Checking if password matches
//            if (passwordEncoder.matches(password, user.getPassword())) {
//
//                // Check if the requested role matches the user's role
//                if (user.getRole().equals(requestedRole)) {
//                    // If roles match, generate the JWT token
//                    String token = jwtService.generateToken(user.getEmail(), user.getRole());
//
//                    // Returning success response with token
//                    return new BaseApiResponse(SUCCESS_OK, SUCCESS, "Login successful", token);
//                } else {
//                    // If the role doesn't match, return an unauthorized response
//                    return new BaseApiResponse(BAD_REQUEST, FAILURE, "Role mismatch", Collections.emptyList());
//                }
//            } else {
//                return new BaseApiResponse(BAD_REQUEST, FAILURE, MESSAGE_INVALID_CREDENTIALS, Collections.emptyList());
//            }
//        } else {
//            return new BaseApiResponse(NOT_FOUND, FAILURE, MESSAGE_NOT_FOUND, Collections.emptyList());
//        }
//    }


//    public BaseApiResponse createUser(UserRequest userRequest) {
//        User user = new User();
//        try {
//            // Check if user with given userId exists
//            Optional<User> existingUser = userRepository.findById(userRequest.getUserId());
//
//            if (existingUser.isPresent()) {
//                // If user exists, update the user details
//                user = existingUser.get();
//                user.setPassword(passwordEncoder.encode(userRequest.getPassword()));
//                user.setContact(userRequest.getContact());
//                user.setAddress(userRequest.getAddress());
//                user.setRole(userRequest.getRole());
//
//                user = userRepository.save(user);
//            } else {
//                // If user doesn't exist, create a new user
//                user.setUserId(userRequest.getUserId());
//                user.setName(userRequest.getName());
//                user.setEmail(userRequest.getEmail());
//                user.setPassword(passwordEncoder.encode(userRequest.getPassword()));
//                user.setContact(userRequest.getContact());
//                user.setAddress(userRequest.getAddress());
//                user.setRole(userRequest.getRole());
//
//                user = userRepository.save(user);
//            }
//
//            UserResponse userResponse = mapToUserResponse(user);
//
//            return new BaseApiResponse(SUCCESS_OK, SUCCESS, MESSAGE_SAVE, userResponse);
//        } catch (Exception e) {
//            // Handle exceptions and return an error response
//            return new BaseApiResponse(INTERNAL_SERVER_ERROR, FAILURE, MESSAGE_COMMON_SERVER_ERROR, Collections.emptyList());
//        }
//    }


//------------------------------------------------2nd Approach (Fetch All users without pagination)-----------------------------

//    // New method to handle fetching all USERS
//    private BaseApiResponse getAllUsers() {
//        try {
//            List<User> users = userRepository.findAll();
//                List<UserResponse> userResponses = users.stream()
//                        .map(this::mapToUserResponse)
//                        .collect(Collectors.toList());
//                return new BaseApiResponse(SUCCESS_OK, SUCCESS, MESSAGE_FETCH_DATA, userResponses);
//        } catch (Exception e) {
//            return new BaseApiResponse(INTERNAL_SERVER_ERROR, FAILURE, MESSAGE_COMMON_SERVER_ERROR, Collections.emptyList());
//        }
//    }
//
//    @Override
//    public BaseApiResponse getUserByUserId(Long userId, boolean findAll) {
//        try {
//            if (findAll) {
//                return getAllUsers();
//            } else {
//                Optional<User> userOptional = userRepository.findById(userId);
//                if (userOptional.isPresent()) {
//                    User user = userOptional.get();
//                    UserResponse userResponse = mapToUserResponse(user);
//                    return new BaseApiResponse(SUCCESS_OK, SUCCESS, MESSAGE_FETCH_DATA, userResponse);
//                } else {
//                    return new BaseApiResponse(NOT_FOUND, FAILURE, MESSAGE_NOT_FOUND, Collections.emptyList());
//                }
//            }
//        } catch (Exception e) {
//            return new BaseApiResponse(INTERNAL_SERVER_ERROR, FAILURE, MESSAGE_COMMON_SERVER_ERROR, Collections.emptyList());
//        }
//    }

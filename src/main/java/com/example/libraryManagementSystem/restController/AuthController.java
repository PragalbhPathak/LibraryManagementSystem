//package com.example.libraryManagementSystem.controller;
//
//import com.example.libraryManagementSystem.dto.requestDto.AuthRequest;
//import com.example.libraryManagementSystem.entity.User;
//import com.example.libraryManagementSystem.repository.UserRepository;
//import com.example.libraryManagementSystem.service.JwtService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.web.bind.annotation.*;
//
//import static com.example.libraryManagementSystem.constraint.REST_MAPPING_CONSTRAINT.BASE_URL;
//
//
//@RestController
//@RequestMapping(BASE_URL)
//public class AuthController {
//
//    @Autowired
//    public PasswordEncoder passwordEncoder;
//    @Autowired
//    public UserRepository userRepository;
//    @Autowired
//    public AuthenticationManager authenticationManager;
//    @Autowired
//    private JwtService jwtService;
//
//    @PostMapping("/register")
//    public ResponseEntity<?> register(@RequestBody User user) {
//        user.setPassword(passwordEncoder.encode(user.getPassword()));
//        User savedUser = userRepository.save(user);
//        return ResponseEntity.ok(savedUser);
//    }
//    @PostMapping("/login")
//    public ResponseEntity<String> login(@RequestBody AuthRequest authRequest) {
//        Authentication authentication = authenticationManager.authenticate(
//                new UsernamePasswordAuthenticationToken(authRequest.getEmail(), authRequest.getPassword())
//        );
//        SecurityContextHolder.getContext().setAuthentication(authentication);
//

    //        String jwt = jwtUtils.generateToken(user.getUsername());
//        return ResponseEntity.ok(jwtService.generateToken(authRequest.getEmail()));
//    }
//    @PostMapping("/login")
//    public ResponseEntity<String> login(@RequestBody AuthRequest authRequest) {
//        // Authenticate the user using the email and password
//        Authentication authentication = authenticationManager.authenticate(
//                new UsernamePasswordAuthenticationToken(authRequest.getEmail(), authRequest.getPassword())
//        );
//
//        // Set authentication in the security context
//        SecurityContextHolder.getContext().setAuthentication(authentication);
//
//        // Retrieve the user details from the authentication object
//        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
//
//        // Assuming the role is a single String field in the user details or user entity
//        String role = userDetails.getAuthorities().stream()
//                .map(GrantedAuthority::getAuthority)  // Role is in the authority (GrantedAuthority)
//                .findFirst()  // Get the first (and potentially only) role
//                .orElse("ROLE_USER");  // Default to "ROLE_USER" if no role found
//
//        // Generate the JWT token with the user's email and role
//        String jwtToken = jwtService.generateToken(authRequest.getEmail(), role);
//
//        // Return the JWT token in the response
//        return ResponseEntity.ok(jwtToken);
//    }
//
//}

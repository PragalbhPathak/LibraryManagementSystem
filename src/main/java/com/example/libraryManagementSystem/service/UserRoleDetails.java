//package com.example.libraryManagementSystem.service;
//
//import com.example.libraryManagementSystem.entity.User;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.security.core.userdetails.UserDetails;
//
//import java.util.Collection;
//import java.util.List;
//import java.util.stream.Collectors;
//import java.util.stream.Stream;
//
//
//public class UserRoleDetails implements UserDetails{
//
//
//    private final String email; // Changed from 'name' to 'username' for clarity
//    private final String password;
//    private final List<GrantedAuthority> authorities;
//
//    public UserRoleDetails(User user) {
//        this.email = user.getEmail(); // Assuming 'name' is used as 'username'
//        this.password = user.getPassword();
//        this.authorities = Stream.of(user.getRole().split(","))
//                .map(SimpleGrantedAuthority::new)
//                .collect(Collectors.toList());
//    }
//
//    @Override
//    public Collection<? extends GrantedAuthority> getAuthorities() {
//        return authorities;
//    }
//
//    @Override
//    public String getPassword() {
//        return password;
//    }
//
//    @Override
//    public String getUsername() {
//        return email;
//    }
//
//    @Override
//    public boolean isAccountNonExpired() {
//        return true; // Implement your logic if you need this
//    }
//
//    @Override
//    public boolean isAccountNonLocked() {
//        return true; // Implement your logic if you need this
//    }
//
//    @Override
//    public boolean isCredentialsNonExpired() {
//        return true; // Implement your logic if you need this
//    }
//
//    @Override
//    public boolean isEnabled() {
//        return true; // Implement your logic if you need this
//    }
//}

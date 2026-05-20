package com.sndf.enquiry.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sndf.enquiry.dto.LoginRequest;
import com.sndf.enquiry.dto.LoginResponse;
import com.sndf.enquiry.entity.User;
import com.sndf.enquiry.repository.UserRepository;
import com.sndf.enquiry.security.JwtUtil;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    public LoginResponse login(
            LoginRequest request) {

        User user = userRepository
                .findByUsername(
                        request.getUsername())
                .orElseThrow();

        if (!user.getPassword()
                .equals(request.getPassword())) {

            throw new RuntimeException(
                    "Invalid Password");
        }

        String token =
        		jwtUtil.generateToken(
        		        user.getUsername(),
        		        user.getRole().name(),
        		        user.getDepartment());

        return new LoginResponse(
                token,
                user.getRole().name(),
                user.getDepartment());
    }
}
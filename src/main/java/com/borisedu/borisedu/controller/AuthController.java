package com.borisedu.borisedu.controller;

import com.borisedu.borisedu.dto.request.LoginRequest;
import com.borisedu.borisedu.dto.request.RegisterRequest;
import com.borisedu.borisedu.dto.response.AuthResponse;
import com.borisedu.borisedu.dto.response.LoginResponse;
import com.borisedu.borisedu.entity.UserEntity;
import com.borisedu.borisedu.repository.UserRepo;
import com.borisedu.borisedu.service.auth.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;
    private final AuthService authService;

    @PostMapping("/login")
    public LoginResponse credentialLogin(@RequestBody LoginRequest credentialsLoginRequest) {
        return authService.credentialsLogin(credentialsLoginRequest);
    }

    @GetMapping("/logout")
    public void logout(@RequestParam("refresh_token") String refreshToken) {
        authService.logout(refreshToken);
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {

        // 1. Kiểm tra xem số điện thoại đã tồn tại trong MySQL chưa
        if (userRepo.findByPhone(request.getPhone()).isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Số điện thoại đã được đăng ký!");
        }

        UserEntity newUser = UserEntity.builder()
                .phone(request.getPhone())
                .password(passwordEncoder.encode(request.getPassword())) // Mã hóa mật khẩu
                .email(request.getEmail())
                .username(request.getUsername())
                .build();

        userRepo.save(newUser);

        return ResponseEntity.ok("Đăng ký tài khoản thành công!");
    }
}

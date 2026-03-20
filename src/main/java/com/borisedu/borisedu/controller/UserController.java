package com.borisedu.borisedu.controller;

import com.borisedu.borisedu.dto.request.ChangePasswordRequest;
import com.borisedu.borisedu.dto.request.PhoneRequest;
import com.borisedu.borisedu.dto.request.ResetPasswordRequest;
import com.borisedu.borisedu.entity.UserEntity;
import com.borisedu.borisedu.exception.custom.NotFoundException;
import com.borisedu.borisedu.repository.UserRepo;
import com.borisedu.borisedu.service.OTPService;
import com.borisedu.borisedu.service.UserService;
import com.borisedu.borisedu.utils.annotation.ApiMessage;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final OTPService otpService;
    private final UserService userService;
    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;

    @ApiMessage("Đổi mật khẩu thành công!")
    @PostMapping("/change-password")
    public ResponseEntity<Void> changePassword(@RequestBody @Valid ChangePasswordRequest changePasswordRequest) {
        return ResponseEntity.ok(userService.changePassword(changePasswordRequest));
    }

    @ApiMessage("Gửi yêu cầu cập nhật mật khẩu thành công!")
    @PostMapping("request-reset-password")
    public ResponseEntity<Void> requestResetPassword(@RequestBody @Valid PhoneRequest request) {
        return ResponseEntity.ok(userService.sendRequestResetPassword(request));
    }

    @ApiMessage("Cập nhật mật khẩu thành công!")
    @PostMapping("reset-password")
    public Void resetPassword(@RequestBody ResetPasswordRequest request) {
        boolean isCorrectOtp = otpService.checkOtp(request.getCode());
        if(isCorrectOtp) {
            UserEntity user = userRepo.findByOtpEntity_Code(request.getCode()).orElseThrow(() -> new NotFoundException("Không tìm thấy tài khoản!"));
            user.setPassword(passwordEncoder.encode(request.getPassword()));
            userRepo.save(user);
        }
        return null;
    }

}

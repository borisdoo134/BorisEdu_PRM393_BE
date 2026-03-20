package com.borisedu.borisedu.service;

import com.borisedu.borisedu.entity.OTPEntity;
import com.borisedu.borisedu.entity.UserEntity;
import com.borisedu.borisedu.exception.custom.NotFoundException;
import com.borisedu.borisedu.exception.custom.OtpException;
import com.borisedu.borisedu.repository.OtpRepo;
import com.borisedu.borisedu.repository.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class OTPService {

    private static final String CHARACTERS = "0123456789";
    private static final int OTP_LENGTH = 6;
    private final SecureRandom secureRandom = new SecureRandom();
    private final OtpRepo otpRepo;
    private final EmailSenderService emailSenderService;
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
    private final UserRepo userRepo;

    public Void generateOTP(UserEntity user, String title) {
        if (user.getOtpEntity() != null) {
            OTPEntity otpCode = user.getOtpEntity();
            user.setOtpEntity(null);
            userRepo.save(user);
            otpRepo.delete(otpCode);
        }
        StringBuilder otp = new StringBuilder();
        for (int i = 0; i < OTP_LENGTH; i++) {
            int index = secureRandom.nextInt(CHARACTERS.length());
            otp.append(CHARACTERS.charAt(index));
        }
        Map<String, Object> otpMap = new HashMap<>();
        otpMap.put("otp", otp.toString());

        OTPEntity otpCode = new OTPEntity();
        otpCode.setCode(otp.toString());
        otpCode.setUser(user);
        otpRepo.save(otpCode);

        emailSenderService.sendEmail(user.getEmail(), title, "mail-template", otpMap);
        return null;
    }


    public Void sendChangePasswordRequest(String phoneRequest) {
        UserEntity user = userRepo.findByPhone(phoneRequest)
                .orElseThrow(() -> new NotFoundException("Số điện thoại này chưa được đăng kí!"));
        return this.generateOTP(user, "Yêu cầu đổi mật khẩu!");
    }


    public boolean checkOtp(String code) {
        OTPEntity otpEntity = otpRepo.findByCode(code).orElseThrow(() -> new NotFoundException("Mã OTP sai!"));
        if(otpEntity.getExpiredAt().isBefore(Instant.now())) {
            throw new OtpException("Mã Otp đã hết hạn!");
        }
        return true;
    }
}
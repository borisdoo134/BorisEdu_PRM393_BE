package com.borisedu.borisedu.config;

import com.borisedu.borisedu.entity.OTPEntity;
import com.borisedu.borisedu.entity.UserEntity;
import com.borisedu.borisedu.repository.OtpRepo;
import com.borisedu.borisedu.repository.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class OTPConfig {
    private final OtpRepo otpRepo;
    private final UserRepo userRepo;

    @Scheduled(fixedRate = 90000) // Chạy mỗi 90 giây
    @Transactional
    public void removeAllExpiredOTP() {
        Set<OTPEntity> expiredOTPCode = otpRepo.findAllExpiredOTP(Instant.now());
        if (expiredOTPCode.isEmpty()) {
            System.out.println("Không có OTP hết hạn!");
            return;
        }

        Set<UserEntity> remainUsers = new HashSet<>();
        for (OTPEntity otpEntity : expiredOTPCode) {
            UserEntity user = otpEntity.getUser();
            if (user != null) {
                user.setOtpEntity(null);
                    remainUsers.add(user);
            }
        }
        userRepo.saveAll(remainUsers);
        otpRepo.deleteAll(expiredOTPCode);

        System.out.println("Deleted expired OTP successfully! (" + expiredOTPCode.size() + ")");
    }
}

package com.borisedu.borisedu.service;

import com.borisedu.borisedu.dto.request.ChangePasswordRequest;
import com.borisedu.borisedu.dto.request.PhoneRequest;
import com.borisedu.borisedu.entity.UserEntity;
import com.borisedu.borisedu.exception.custom.NotFoundException;
import com.borisedu.borisedu.helper.UserServiceHelper;
import com.borisedu.borisedu.repository.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepo userRepo;
    private final OTPService otpService;
    private final UserServiceHelper userServiceHelper;
    private final PasswordEncoder passwordEncoder;

    public Void sendRequestResetPassword(PhoneRequest request) {
        Optional<UserEntity> optionalUserEntity = userRepo.findByPhone(request.getPhone());
        if(optionalUserEntity.isPresent()) {
            UserEntity existedUserEntity = optionalUserEntity.get();
            otpService.generateOTP(existedUserEntity, "Yêu cầu xác thực email!");
        } else {
            throw new NotFoundException("Không tìm thấy tài khoản!");
        }
        return null;
    }

    public Void changePassword(ChangePasswordRequest request) {
        UserEntity currentUser = userServiceHelper.checkIfLogin();
        currentUser.setPassword(passwordEncoder.encode(request.getPassword()));
        userRepo.save(currentUser);
        return null;
    }

}

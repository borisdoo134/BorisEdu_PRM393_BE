package com.borisedu.borisedu.helper;

import com.borisedu.borisedu.entity.UserEntity;
import com.borisedu.borisedu.exception.custom.AccountException;
import com.borisedu.borisedu.exception.custom.NotFoundException;
import com.borisedu.borisedu.repository.UserRepo;
import com.borisedu.borisedu.service.auth.JwtService;
import org.springframework.stereotype.Component;

@Component
public class UserServiceHelper {

    private final UserRepo userRepo;

    public UserServiceHelper(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    public UserEntity extractUserFromToken() {
        String phone = JwtService.extractUsernameFromToken().orElse(null);

        if (phone != null ) {
            UserEntity userEntity = userRepo.findByPhone(phone)
                    .orElseThrow(() -> new NotFoundException("Không tìm thấy người dùng"));
            return userEntity;
        }
        return null;
    }

    public UserEntity checkIfLogin() {
        UserEntity user = extractUserFromToken();
        if (user == null) {
            throw new AccountException("Vui lòng đăng nhập để thực hiện chức năng này!");
        }
        return user;
    }
}

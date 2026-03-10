package com.borisedu.borisedu.service.auth;

import com.borisedu.borisedu.dto.request.LoginRequest;
import com.borisedu.borisedu.dto.response.LoginResponse;
import com.borisedu.borisedu.entity.UserEntity;
import com.borisedu.borisedu.exception.custom.AccountException;
import com.borisedu.borisedu.exception.custom.InvalidTokenException;
import com.borisedu.borisedu.helper.AuthServiceHelper;
import com.borisedu.borisedu.repository.RoleRepo;
import com.borisedu.borisedu.repository.UserRepo;
import com.borisedu.borisedu.utils.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {


    private final UserRepo userRepo;
    private final SecurityUtil securityUtil;
    private final AuthServiceHelper authServiceHelper;
    private final JwtService jwtService;
    private final ModelMapper modelMapper;
    private final RoleRepo roleRepo;
    private final PasswordEncoder passwordEncoder;


    public LoginResponse credentialsLogin(LoginRequest loginRequest) {
        String phone = authServiceHelper.authenticatedCredentialsLogin(loginRequest);
        UserEntity userEntity = userRepo.findByPhone(phone)
                .orElseThrow(() -> new AccountException("Tài khoản không tồn tại!"));
        return authServiceHelper.createLoginResponse(userEntity, phone);
    }

    public void logout(String refreshToken) {
        NimbusJwtDecoder jwtDecoder = NimbusJwtDecoder
                .withSecretKey(securityUtil.getSecretKey())
                .macAlgorithm(securityUtil.JWT_ALGORITHMS)
                .build();

        try {
            Jwt jwt = jwtDecoder.decode(refreshToken);
            String phone = jwt.getSubject();
            UserEntity userEntity = userRepo.findByPhone(phone)
                    .orElseThrow(() -> new AccountException("User not found!"));
            userEntity.setRefreshToken(null);
            userRepo.save(userEntity);
        } catch (Exception e) {
            throw new InvalidTokenException("Refresh token is invalid!");
        }
    }

}

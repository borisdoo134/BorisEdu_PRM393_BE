package com.borisedu.borisedu.helper;

import com.borisedu.borisedu.dto.request.LoginRequest;
import com.borisedu.borisedu.dto.response.LoginResponse;
import com.borisedu.borisedu.dto.response.RoleResponse;
import com.borisedu.borisedu.dto.response.UserResponse;
import com.borisedu.borisedu.entity.UserEntity;
import com.borisedu.borisedu.repository.UserRepo;
import com.borisedu.borisedu.service.auth.JwtService;
import com.borisedu.borisedu.utils.BuildResponse;
import com.borisedu.borisedu.utils.SecurityUtil;
import com.borisedu.borisedu.utils.enums.RoleEnum;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class AuthServiceHelper {

    private final AuthenticationManager authenticationManager;
    private final ModelMapper modelMapper;
    private final JwtService jwtService;
    private final UserRepo userRepo;
    private final SecurityUtil securityUtil;

    public AuthServiceHelper(AuthenticationManager authenticationManager, ModelMapper modelMapper, JwtService jwtService, UserRepo userRepo, SecurityUtil securityUtil) {
        this.authenticationManager = authenticationManager;
        this.modelMapper = modelMapper;
        this.jwtService = jwtService;
        this.userRepo = userRepo;
        this.securityUtil = securityUtil;
    }

    public String authenticatedCredentialsLogin(LoginRequest loginRequest) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(loginRequest.getPhone(), loginRequest.getPassword());
        Authentication authentication = authenticationManager.authenticate(authenticationToken);
        return authentication.getName();
    }

    public LoginResponse createLoginResponse(UserEntity userEntity, String phone) {
        UserResponse userResponse = modelMapper.map(userEntity, UserResponse.class);
        Set<RoleResponse> roleResponses = userEntity.getRoles().stream()
                .map(role -> {
                    RoleResponse response = new RoleResponse();
                    response.setId(role.getId());
                    response.setName(role.getName());
                    return response;
                })
                .collect(Collectors.toSet());
        userResponse.setRoles(roleResponses);
        String accessToken = jwtService.createJWTToken(phone, userEntity.getId(), securityUtil.accessTokenExpiration);
        String refreshToken = jwtService.createJWTToken(phone,  userResponse.getId(), securityUtil.refreshTokenExpiration);

        userEntity.setRefreshToken(refreshToken);
        UserEntity newAccount = userRepo.save(userEntity);
        UserResponse newUserResponse = modelMapper.map(newAccount, UserResponse.class);
        newUserResponse.setRoles(roleResponses);

        Instant now = Instant .now();
        Instant expireAt = now.plusSeconds(securityUtil.accessTokenExpiration);
        return BuildResponse.buildLoginResponse(newUserResponse, accessToken, expireAt, refreshToken);
    }
}

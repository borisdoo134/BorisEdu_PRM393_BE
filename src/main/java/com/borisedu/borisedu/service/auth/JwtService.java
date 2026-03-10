package com.borisedu.borisedu.service.auth;

import com.borisedu.borisedu.dto.response.LoginResponse;
import com.borisedu.borisedu.dto.response.UserResponse;
import com.borisedu.borisedu.entity.UserEntity;
import com.borisedu.borisedu.exception.custom.InvalidTokenException;
import com.borisedu.borisedu.repository.UserRepo;
import com.borisedu.borisedu.utils.BuildResponse;
import com.borisedu.borisedu.utils.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;

import javax.security.auth.login.AccountException;
import java.time.Instant;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class JwtService {

    private final SecurityUtil securityUtil;
    private final JwtEncoder jwtEncoder;
    private final UserRepo userRepo;
    private final ModelMapper modelMapper;

    public String createJWTToken(String email, Long userId, Long expireTime) {
        Instant now = Instant.now();
        Instant expireAt = now.plusSeconds(expireTime);

        JwtClaimsSet jwtClaimsSet = JwtClaimsSet.builder()
                .issuedAt(now)
                .expiresAt(expireAt)
                .subject(email)
                .claim("userId", userId)
                .build();

        JwsHeader jwsHeader = JwsHeader.with(securityUtil.JWT_ALGORITHMS).build();
        return jwtEncoder.encode(JwtEncoderParameters.from(jwsHeader, jwtClaimsSet)).getTokenValue();
    }

    public LoginResponse letRefreshToken(String refreshToken) {
        NimbusJwtDecoder jwtDecoder = NimbusJwtDecoder
                .withSecretKey(securityUtil.getSecretKey())
                .macAlgorithm(securityUtil.JWT_ALGORITHMS)
                .build();

        try {
            Jwt jwt = jwtDecoder.decode(refreshToken);
            String phone = jwt.getSubject();
            Long userId = jwt.getClaim("userId");

            UserEntity userEntity = userRepo.findByPhone(phone)
                    .orElseThrow(() -> new AccountException("User not found!"));
            UserResponse userResponse = modelMapper.map(userEntity, UserResponse.class);

            String accessToken = createJWTToken(phone, userId, securityUtil.accessTokenExpiration);
            String newRefreshToken = createJWTToken(phone, userId, securityUtil.refreshTokenExpiration);
            Instant now = Instant.now();
            Instant expireAt = now.plusSeconds(securityUtil.accessTokenExpiration);

            userEntity.setRefreshToken(newRefreshToken);
            userRepo.save(userEntity);

            return BuildResponse.buildLoginResponse(userResponse, accessToken, expireAt, refreshToken);

        } catch (Exception e) {
            throw new InvalidTokenException("Refresh token is invalid!");
        }
    }

//    public static Optional<String> extractUsernameFromToken() {
//        SecurityContext securityContext = SecurityContextHolder.getContext();
//        Authentication authentication = securityContext.getAuthentication();
//
//        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
//            return Optional.empty();
//        } else if (authentication.getPrincipal() instanceof UserDetails userDetails) {
//            return Optional.ofNullable(userDetails.getUsername());
//        } else if (authentication.getPrincipal() instanceof Jwt jwt) {
//            return Optional.ofNullable(jwt.getSubject());
//        } else if (authentication.getPrincipal() instanceof String username) {
//            return Optional.of(username);
//        } else {
//            return Optional.empty();
//        }
//    }

    public Jwt decodeToken(String token) {
        NimbusJwtDecoder jwtDecoder = NimbusJwtDecoder
                .withSecretKey(securityUtil.getSecretKey())
                .macAlgorithm(securityUtil.JWT_ALGORITHMS)
                .build();
        try {
            return jwtDecoder.decode(token);
        } catch (JwtException e) {
            throw new InvalidTokenException("Token không hợp lệ hoặc đã hết hạn!");
        }
    }
}

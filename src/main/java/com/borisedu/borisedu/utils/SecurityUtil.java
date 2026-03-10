package com.borisedu.borisedu.utils;

import com.nimbusds.jose.util.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

@Component
public class SecurityUtil {

    @Value("${jwt.base64.secret}")
    public String jwtKey;

    @Value("${jwt.access.token.validity.in.seconds}")
    public Long accessTokenExpiration;

    @Value("${jwt.refresh.token.validity.in.seconds}")
    public Long refreshTokenExpiration;

    public final MacAlgorithm JWT_ALGORITHMS = MacAlgorithm.HS512;

    public SecretKey getSecretKey() {
        byte[] keyBytes = Base64.from(jwtKey).decode();
        return new SecretKeySpec(keyBytes, 0, keyBytes.length, JWT_ALGORITHMS.getName());
    }
}

package com.borisedu.borisedu.config;

import com.borisedu.borisedu.config.custom.AuthenticationEntryPointCustom;
import com.borisedu.borisedu.entity.UserEntity;
import com.borisedu.borisedu.exception.custom.InvalidTokenException;
import com.borisedu.borisedu.exception.custom.NotFoundException;
import com.borisedu.borisedu.repository.UserRepo;
import com.borisedu.borisedu.utils.SecurityUtil;
import com.nimbusds.jose.jwk.source.ImmutableSecret;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.io.NotActiveException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    public static final String[] PUBLIC_ENDPOINTS = {
            "/ws/**",
            "/api/v1/auth/**",
            "/storage/**",
            "/api/v1/files",
            "/api/v1/users/request-reset-password",
            "/api/v1/users/reset-password",
            "/api/v1/otp/**",
    };

    private final SecurityUtil securityUtil;
    private final UserRepo userRepo;

    @Bean
    public SecurityFilterChain configure(HttpSecurity http, AuthenticationEntryPointCustom authenticationEntryPointCustom) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .authorizeHttpRequests(
                        request -> request
                                .requestMatchers(PUBLIC_ENDPOINTS).permitAll()
                                .requestMatchers("/ws/**").permitAll()
                                .anyRequest().authenticated()
                )
                .oauth2ResourceServer(
                        oauth2 -> oauth2
                                .jwt(Customizer.withDefaults())
                                .authenticationEntryPoint(authenticationEntryPointCustom)
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public JwtEncoder jwtEncoder() {
        return new NimbusJwtEncoder(new ImmutableSecret<>(securityUtil.getSecretKey()));
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        NimbusJwtDecoder jwtDecoder = NimbusJwtDecoder
                .withSecretKey(securityUtil.getSecretKey())
                .macAlgorithm(securityUtil.JWT_ALGORITHMS)
                .build();

        return token -> {
            try {
                return jwtDecoder.decode(token);
            } catch (InvalidTokenException e) {
                throw new InvalidTokenException(e.getMessage());
            }
        };
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        jwtGrantedAuthoritiesConverter.setAuthorityPrefix("");

        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwtGrantedAuthoritiesConverter);

        return jwtAuthenticationConverter;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return username -> {
            UserEntity user = userRepo.findByPhone(username)
                    .orElseThrow(() -> new NotFoundException("Không tìm thấy user với SĐT: " + username));

            var authorities = user.getRoles().stream()
                    .map(role -> new SimpleGrantedAuthority(role.getName().toString()))
                    .collect(Collectors.toList());

            return org.springframework.security.core.userdetails.User
                    .withUsername(user.getPhone())
                    .password(user.getPassword())
                    .authorities(authorities)
                    .build();
        };
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // Cho phép mọi origin (bao gồm Flutter Web trên localhost)
        configuration.setAllowedOriginPatterns(List.of("*"));

        // Cho phép các HTTP method cần thiết
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));

        // Cho phép mọi header
        configuration.setAllowedHeaders(List.of("*"));

        // Cho phép gửi credentials (Authorization header, cookies...)
        configuration.setAllowCredentials(true);

        // Cache preflight request trong 1 giờ
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
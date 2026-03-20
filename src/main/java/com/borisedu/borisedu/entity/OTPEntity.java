package com.borisedu.borisedu.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "otp")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OTPEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "otp_id")
    Long otpId;

    String code;

    @OneToOne
    @JoinColumn(name = "user_id")
    @ToString.Exclude
    @JsonIgnore
    UserEntity user;

    Instant createdAt;

    Instant expiredAt;

    @PrePersist
    public void handlePrePersist() {
        createdAt = Instant.now();
        expiredAt = createdAt.plus(3, ChronoUnit.MINUTES);
    }
}
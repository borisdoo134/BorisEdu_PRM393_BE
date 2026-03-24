package com.borisedu.borisedu.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Entity
@Table(name = "system_events")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SystemEventEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "system_event_id")
    Long id;

    @Column(nullable = false)
    String title;

    @Column(nullable = false)
    String imageUrl;

    @Column(nullable = false, columnDefinition = "LONGTEXT")
    String content;

    LocalDateTime startDate;

    LocalDateTime endDate;

    String targetAudience;

    @Column(columnDefinition = "TEXT")
    String termsAndConditions;

    @Column(nullable = false)
    @Builder.Default
    boolean active = true;

    LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
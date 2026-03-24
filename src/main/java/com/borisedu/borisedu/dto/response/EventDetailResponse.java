package com.borisedu.borisedu.dto.response;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class EventDetailResponse {
    private Long id;
    private String title;
    private String imageUrl;
    private String content;

    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String targetAudience;
    private String termsAndConditions;

    private LocalDateTime createdAt;
}
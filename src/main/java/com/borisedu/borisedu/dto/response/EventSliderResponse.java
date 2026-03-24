package com.borisedu.borisedu.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EventSliderResponse {
    private Long id;
    private String imageUrl;
}
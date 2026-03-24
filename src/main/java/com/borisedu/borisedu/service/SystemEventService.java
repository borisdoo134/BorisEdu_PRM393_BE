package com.borisedu.borisedu.service;

import com.borisedu.borisedu.dto.response.EventDetailResponse;
import com.borisedu.borisedu.dto.response.EventSliderResponse;
import com.borisedu.borisedu.entity.SystemEventEntity;
import com.borisedu.borisedu.repository.SystemEventRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SystemEventService {

    private final SystemEventRepo eventRepo;

    public List<EventSliderResponse> getSliders() {
        return eventRepo.findByActiveTrueOrderByCreatedAtDesc().stream()
                .map(event -> EventSliderResponse.builder()
                        .id(event.getId())
                        .imageUrl(event.getImageUrl())
                        .build())
                .collect(Collectors.toList());
    }

    public EventDetailResponse getEventDetail(Long id) {
        SystemEventEntity event = eventRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sự kiện này!"));

        return EventDetailResponse.builder()
                .id(event.getId())
                .title(event.getTitle())
                .imageUrl(event.getImageUrl())
                .content(event.getContent())
                // Thêm 4 dòng này vào
                .startDate(event.getStartDate())
                .endDate(event.getEndDate())
                .targetAudience(event.getTargetAudience())
                .termsAndConditions(event.getTermsAndConditions())
                // -----------------
                .createdAt(event.getCreatedAt())
                .build();
    }
}
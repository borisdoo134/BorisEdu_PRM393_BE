package com.borisedu.borisedu.controller;

import com.borisedu.borisedu.dto.response.ApiResponse;
import com.borisedu.borisedu.service.SystemEventService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/events")
@RequiredArgsConstructor
public class SystemEventController {

    private final SystemEventService eventService;

    @GetMapping("/sliders")
    public ResponseEntity<?> getSliders() {
        return ResponseEntity.ok(ApiResponse.builder()
                .status(200)
                .message("Lấy danh sách slider thành công")
                .data(eventService.getSliders())
                .build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getEventDetail(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.builder()
                .status(200)
                .message("Lấy chi tiết sự kiện thành công")
                .data(eventService.getEventDetail(id))
                .build());
    }
}
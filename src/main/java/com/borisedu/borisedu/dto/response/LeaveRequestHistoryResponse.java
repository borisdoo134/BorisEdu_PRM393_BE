package com.borisedu.borisedu.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class LeaveRequestHistoryResponse {
    private Long id;
    private LocalDate fromDate;
    private LocalDate toDate;
    private String reason;
    private LocalDateTime createdAt;
}
package com.borisedu.borisedu.dto.request;

import lombok.Data;
import java.time.LocalDate;

@Data
public class LeaveCreationRequest {
    private Long studentId; // ID của bé cần xin nghỉ
    private LocalDate fromDate; // Từ ngày
    private LocalDate toDate;   // Đến ngày
    private String reason;      // Lý do
}
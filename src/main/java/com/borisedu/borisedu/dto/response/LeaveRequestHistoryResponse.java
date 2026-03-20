package com.borisedu.borisedu.dto.response;

import com.borisedu.borisedu.utils.enums.LeaveRequestStatusEnum;
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
    private LeaveRequestStatusEnum status;
    private LocalDateTime createdAt;
    private String teacherNote; // Lời nhắn của giáo viên khi duyệt/từ chối
}
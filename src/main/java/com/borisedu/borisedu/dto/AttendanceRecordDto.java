package com.borisedu.borisedu.dto;

import com.borisedu.borisedu.utils.enums.AttendanceStatusEnum;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDate;

@Data
@Builder
public class AttendanceRecordDto {
    private LocalDate date;

    private String teacherName;

    private Integer period;

    private AttendanceStatusEnum status;
}

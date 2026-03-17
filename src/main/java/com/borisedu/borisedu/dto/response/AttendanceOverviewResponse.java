package com.borisedu.borisedu.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AttendanceOverviewResponse {
    Long subjectId;
    String subjectName;
    String className;
    String academicYear;

    int presentCount;
    int absentCount;
    int totalConducted; // Số tiết ĐÃ HỌC đến thời điểm hiện tại (present + absent)
    int percentage;     // Tỷ lệ % có mặt

    int totalYearSlots;
    int maxAbsentAllowed;
    boolean isBannedFromExam;
}
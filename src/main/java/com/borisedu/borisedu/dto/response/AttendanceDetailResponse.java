package com.borisedu.borisedu.dto.response;

import com.borisedu.borisedu.dto.AttendanceRecordDto;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AttendanceDetailResponse {
    String subjectName;
    String className;

    int percentage;
    int presentCount;
    int absentCount;
    int futureCount;

    int totalYearSlots;
    int maxAbsentAllowed;
    boolean isBannedFromExam;

    List<AttendanceRecordDto> records;
}
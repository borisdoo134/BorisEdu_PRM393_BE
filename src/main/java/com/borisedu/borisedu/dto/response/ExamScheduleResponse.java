package com.borisedu.borisedu.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ExamScheduleResponse {

    Long id;
    String subjectName;
    String examType;
    LocalDate examDate;
    LocalTime startTime;
    LocalTime endTime;
    String room;
    String status;
    String academicYear;

}

package com.borisedu.borisedu.dto.response;

import com.borisedu.borisedu.utils.enums.DayOfWeekEnum;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.time.LocalTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ScheduleResponse {
    Long id;
    DayOfWeekEnum dayOfWeek;
    Integer period;
    LocalTime startTime;
    LocalTime endTime;
    String subjectName;
    String room;
    Long teacherId;
    String teacherName;
    String teacherAvatar;
    LocalDate actualDate;
    Integer month;
    Integer year;
    Integer weekOfYear;
}

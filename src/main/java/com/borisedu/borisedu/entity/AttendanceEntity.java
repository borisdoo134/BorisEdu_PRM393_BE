package com.borisedu.borisedu.entity;

import com.borisedu.borisedu.utils.enums.AttendanceStatusEnum;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.FieldDefaults;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "attendances")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AttendanceEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    StudentEntity student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "schedule_id", nullable = false)
    ScheduleEntity schedule;

    @Column(name = "attendance_date", nullable = false)
    LocalDate attendanceDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    AttendanceStatusEnum status;

}

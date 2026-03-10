package com.borisedu.borisedu.entity;

import com.borisedu.borisedu.utils.enums.DayOfWeekEnum;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalTime;

@Entity
@Table(name = "schedules")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ScheduleEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "day_of_week", nullable = false)
    DayOfWeekEnum dayOfWeek;

    @Column(nullable = false)
    Integer period;

    @Column(name = "start_time")
    LocalTime startTime;

    @Column(name = "end_time")
    LocalTime endTime;

    @Column(name = "room", length = 50)
    String room;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "class_id", nullable = false)
    ClassEntity schoolClass;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subject_id", nullable = false)
    SubjectEntity subject;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_id")
    UserEntity teacher;

}

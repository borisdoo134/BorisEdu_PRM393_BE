package com.borisedu.borisedu.service;

import com.borisedu.borisedu.dto.response.ScheduleResponse;
import com.borisedu.borisedu.entity.ScheduleEntity;
import com.borisedu.borisedu.entity.UserEntity;
import com.borisedu.borisedu.repository.ScheduleRepo;
import com.borisedu.borisedu.repository.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.WeekFields;
import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class ScheduleService {

    private final UserRepo userRepo;
    private final ScheduleRepo scheduleRepo;

    public List<ScheduleResponse> getTimetableByStudentId(Long studentId) {
        UserEntity student = userRepo.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy học sinh với ID: " + studentId));

        // Kiểm tra xem học sinh đã có lớp chưa
        if (student.getSchoolClass() == null) {
            throw new RuntimeException("Học sinh này chưa được xếp lớp!");
        }

        Long classId = student.getSchoolClass().getId();
        List<ScheduleEntity> schedules = scheduleRepo.findByClassId(classId);

        // XỬ LÝ NGÀY THÁNG: Lấy ngày hiện tại làm gốc
        LocalDate today = LocalDate.now();

        // Tìm ngày Thứ 2 của tuần hiện tại
        LocalDate startOfWeek = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));

        // Chuẩn ISO quy định Tuần 1 bắt đầu từ Thứ 2
        WeekFields weekFields = WeekFields.ISO;
        int currentWeekNumber = today.get(weekFields.weekOfWeekBasedYear());

        return schedules.stream().map(schedule -> {
            ScheduleResponse response = new ScheduleResponse();
            response.setId(schedule.getId());
            response.setDayOfWeek(schedule.getDayOfWeek());
            response.setPeriod(schedule.getPeriod());
            response.setStartTime(schedule.getStartTime());
            response.setEndTime(schedule.getEndTime());

            response.setSubjectName(schedule.getSubject().getName());
            response.setRoom(schedule.getRoom());

            if (schedule.getTeacher() != null) {
                response.setTeacherId(schedule.getTeacher().getId());
                response.setTeacherName((schedule.getTeacher().getLastName() + " " + schedule.getTeacher().getFirstName()).trim());
                response.setTeacherAvatar(schedule.getTeacher().getAvatarUrl());
            }

            DayOfWeek javaDayOfWeek = DayOfWeek.valueOf(schedule.getDayOfWeek().name());

            // Tính ra ngày thực tế của tiết học này trong tuần hiện tại
            LocalDate actualScheduleDate = startOfWeek.with(javaDayOfWeek);

            response.setActualDate(actualScheduleDate);
            response.setMonth(actualScheduleDate.getMonthValue());
            response.setYear(actualScheduleDate.getYear());
            response.setWeekOfYear(currentWeekNumber);

            return response;
        }).collect(Collectors.toList());
    }
}

package com.borisedu.borisedu.service;

import com.borisedu.borisedu.dto.AttendanceRecordDto;
import com.borisedu.borisedu.dto.response.AttendanceDetailResponse;
import com.borisedu.borisedu.dto.response.AttendanceOverviewResponse;
import com.borisedu.borisedu.entity.AttendanceEntity;
import com.borisedu.borisedu.entity.SubjectEntity;
import com.borisedu.borisedu.repository.AttendanceRepo;
import com.borisedu.borisedu.utils.enums.AttendanceStatusEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AttendanceService {
    private final AttendanceRepo attendanceRepo;

    // API 1: Màn hình Tổng quan
    public List<AttendanceOverviewResponse> getOverview(Long studentId) {
        List<AttendanceEntity> attendances = attendanceRepo.findByStudentId(studentId);

        Map<SubjectEntity, List<AttendanceEntity>> groupedBySubject = attendances.stream()
                .collect(Collectors.groupingBy(a -> a.getSchedule().getSubject()));

        List<AttendanceOverviewResponse> result = new ArrayList<>();

        for (Map.Entry<SubjectEntity, List<AttendanceEntity>> entry : groupedBySubject.entrySet()) {
            SubjectEntity subject = entry.getKey();
            List<AttendanceEntity> records = entry.getValue();

            int present = (int) records.stream().filter(r -> r.getStatus() == AttendanceStatusEnum.PRESENT).count();
            int absent = (int) records.stream().filter(r -> r.getStatus() == AttendanceStatusEnum.ABSENT).count();
            int conducted = present + absent;

            // Tính tỷ lệ % chuyên cần (Dựa trên số buổi ĐÃ HỌC)
            int percentage = conducted == 0 ? 0 : Math.round(((float) present / conducted) * 100);

            int totalYearSlots = 45;

            int maxAbsentAllowed = (int) (totalYearSlots * 0.20);

            boolean isBanned = absent > maxAbsentAllowed;

            String className = records.isEmpty() ? "" : records.get(0).getSchedule().getSchoolClass().getClassName();

            result.add(AttendanceOverviewResponse.builder()
                    .subjectId(subject.getId())
                    .subjectName(subject.getName())
                    .className("Lớp: " + className)
                    .presentCount(present)
                    .absentCount(absent)
                    .totalConducted(conducted)
                    .percentage(percentage)
                    .totalYearSlots(totalYearSlots)
                    .maxAbsentAllowed(maxAbsentAllowed)
                    .isBannedFromExam(isBanned) // Gửi cờ này về cho Flutter
                    .build());
        }
        return result;
    }
    // API 2: Màn hình Chi tiết 1 Môn
    public AttendanceDetailResponse getDetail(Long studentId, Long subjectId) {
        List<AttendanceEntity> records = attendanceRepo.findByStudentIdAndSubjectId(studentId, subjectId);

        if (records.isEmpty()) {
            throw new RuntimeException("Không có dữ liệu điểm danh cho môn này!");
        }

        int present = (int) records.stream().filter(r -> r.getStatus() == AttendanceStatusEnum.PRESENT).count();
        int total = records.size();
        int percentage = total == 0 ? 0 : Math.round(((float) present / total) * 100);

        String subjectName = records.get(0).getSchedule().getSubject().getName();
        String className = records.get(0).getSchedule().getSchoolClass().getClassName();

        List<AttendanceRecordDto> recordDtos = records.stream().map(r -> {
            String teacherName = r.getSchedule().getTeacher() != null ?
                    r.getSchedule().getTeacher().getLastName() + " " + r.getSchedule().getTeacher().getFirstName() : "Chưa rõ";

            return AttendanceRecordDto.builder()
                    .date(r.getAttendanceDate())
                    .teacherName(teacherName.trim())
                    .period(r.getSchedule().getPeriod())
                    .status(r.getStatus())
                    .build();
        }).collect(Collectors.toList());

        return AttendanceDetailResponse.builder()
                .subjectName(subjectName)
                .className("Lớp: " + className)
                .percentage(percentage)
                .presentCount(present)
                .absentCount(total - present)
                .futureCount(15) // Thường được tính = Tổng số tiết trong kỳ - Số tiết đã học. Ở đây set cứng tạm 15.
                .records(recordDtos)
                .build();
    }
}

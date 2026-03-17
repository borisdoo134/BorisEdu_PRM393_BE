package com.borisedu.borisedu.service;

import com.borisedu.borisedu.dto.AttendanceRecordDto;
import com.borisedu.borisedu.dto.response.AttendanceDetailResponse;
import com.borisedu.borisedu.dto.response.AttendanceOverviewResponse;
import com.borisedu.borisedu.entity.AttendanceEntity;
import com.borisedu.borisedu.entity.ScheduleEntity;
import com.borisedu.borisedu.entity.SubjectEntity;
import com.borisedu.borisedu.repository.AttendanceRepo;
import com.borisedu.borisedu.repository.ScheduleRepo;
import com.borisedu.borisedu.utils.enums.AttendanceStatusEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AttendanceService {
    private final AttendanceRepo attendanceRepo;
    private final ScheduleRepo scheduleRepo;

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
            String academicYear = records.isEmpty() ? "" : records.get(0).getSchedule().getSchoolClass().getAcademicYear();
            result.add(AttendanceOverviewResponse.builder()
                    .subjectId(subject.getId())
                    .subjectName(subject.getName())
                    .className("Lớp: " + className)
                    .academicYear(academicYear)
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
        // 1. Lấy dữ liệu điểm danh trong QUÁ KHỨ (Đang sắp xếp mới nhất lên đầu theo DB)
        List<AttendanceEntity> pastRecords = attendanceRepo.findByStudentIdAndSubjectId(studentId, subjectId);

        if (pastRecords.isEmpty()) {
            throw new RuntimeException("Không có dữ liệu điểm danh cho môn này!");
        }

        Long classId = pastRecords.get(0).getSchedule().getSchoolClass().getId();
        String subjectName = pastRecords.get(0).getSchedule().getSubject().getName();
        String className = pastRecords.get(0).getSchedule().getSchoolClass().getClassName();

        // 2. Tính toán số liệu
        int totalConducted = pastRecords.size();
        int present = (int) pastRecords.stream().filter(r -> r.getStatus() == AttendanceStatusEnum.PRESENT).count();
        int absent = totalConducted - present;
        int percentage = totalConducted == 0 ? 0 : Math.round(((float) present / totalConducted) * 100);

        int totalWeeksInYear = 35;
        // Lấy danh sách lịch học trong tuần của môn này
        List<ScheduleEntity> subjectSchedules = scheduleRepo.findBySchoolClassIdAndSubjectId(classId, subjectId);
        int slotsPerWeek = subjectSchedules.size();

        int totalYearSlots = slotsPerWeek * totalWeeksInYear;
        int futureCount = totalYearSlots - totalConducted;
        if (futureCount < 0) futureCount = 0;

        int maxAbsentAllowed = (int) (totalYearSlots * 0.20);
        boolean isBannedFromExam = absent > maxAbsentAllowed;

        // 3. Map các dòng ĐÃ HỌC vào DTO
        List<AttendanceRecordDto> recordDtos = pastRecords.stream().map(r -> {
            String teacherName = r.getSchedule().getTeacher() != null ?
                    r.getSchedule().getTeacher().getLastName() + " " + r.getSchedule().getTeacher().getFirstName() : "Chưa rõ";
            return AttendanceRecordDto.builder()
                    .date(r.getAttendanceDate())
                    .teacherName(teacherName.trim())
                    .period(r.getSchedule().getPeriod())
                    .status(r.getStatus())
                    .build();
        }).collect(Collectors.toList());

        // ==========================================
        // 4. TIÊN ĐOÁN TƯƠNG LAI & SẮP XẾP LẠI (TĂNG DẦN)
        // ==========================================

        // Đảo ngược Quá khứ: Từ (Mới -> Cũ) thành (Cũ -> Mới)
        Collections.reverse(recordDtos);

        if (futureCount > 0 && !subjectSchedules.isEmpty()) {
            List<AttendanceRecordDto> futureRecords = new ArrayList<>();
            // Lấy ngày cuối cùng của quá khứ (lúc nãy là pastRecords.get(0) vì nó xếp mới nhất lên đầu)
            LocalDate lastAttendanceDate = pastRecords.get(0).getAttendanceDate();
            LocalDate loopDate = lastAttendanceDate.plusDays(1);
            int count = futureCount;

            // Vòng lặp quét tới tương lai cho đến khi đủ số tiết còn thiếu
            while (count > 0) {
                String currentDayName = loopDate.getDayOfWeek().name();

                for (ScheduleEntity schedule : subjectSchedules) {
                    // Nếu ngày đang quét trúng vào ngày có lịch học môn này
                    if (schedule.getDayOfWeek().name().equals(currentDayName)) {
                        String teacherName = schedule.getTeacher() != null ?
                                schedule.getTeacher().getLastName() + " " + schedule.getTeacher().getFirstName() : "Chưa rõ";

                        futureRecords.add(AttendanceRecordDto.builder()
                                .date(loopDate)
                                .teacherName(teacherName.trim())
                                .period(schedule.getPeriod())
                                .status(AttendanceStatusEnum.FUTURE) // Gán trạng thái FUTURE
                                .build());
                        count--;
                        if (count == 0) break;
                    }
                }
                loopDate = loopDate.plusDays(1);
            }

            // Nối toàn bộ lịch Tương lai (Cũ -> Mới) vào đuôi lịch Quá khứ
            recordDtos.addAll(futureRecords);
        }

        // 5. Trả về cho UI
        return AttendanceDetailResponse.builder()
                .subjectName(subjectName)
                .className("Lớp: " + className)
                .percentage(percentage)
                .presentCount(present)
                .absentCount(absent)
                .futureCount(futureCount)
                .totalYearSlots(totalYearSlots)
                .maxAbsentAllowed(maxAbsentAllowed)
                .isBannedFromExam(isBannedFromExam)
                .records(recordDtos)
                .build();
    }}
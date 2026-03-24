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

            // 1. Tách làm 3 biến đếm: Có mặt, Có phép, Không phép
            int present = (int) records.stream().filter(r -> r.getStatus() == AttendanceStatusEnum.PRESENT).count();
            int excused = (int) records.stream().filter(r -> r.getStatus() == AttendanceStatusEnum.EXCUSED_ABSENT).count();
            int unexcused = (int) records.stream().filter(r -> r.getStatus() == AttendanceStatusEnum.UNEXCUSED_ABSENT).count();

            // Tổng số tiết đã tiến hành = Có mặt + Vắng có phép + Vắng không phép
            int conducted = present + excused + unexcused;

            // Tính tỷ lệ % chuyên cần (Dựa trên số buổi ĐÃ HỌC)
            int percentage = conducted == 0 ? 0 : Math.round(((float) present / conducted) * 100);

            int totalYearSlots = 45; // Nếu muốn tính động như hàm getDetail, bạn có thể gọi qua scheduleRepo nhé

            // Cấm thi tính trên số buổi KHÔNG PHÉP
            int maxUnexcusedAllowed = (int) (totalYearSlots * 0.20);
            boolean isBanned = unexcused > maxUnexcusedAllowed;

            String className = records.isEmpty() ? "" : records.get(0).getSchedule().getSchoolClass().getClassName();
            String academicYear = records.isEmpty() ? "" : records.get(0).getSchedule().getSchoolClass().getAcademicYear();

            result.add(AttendanceOverviewResponse.builder()
                    .subjectId(subject.getId())
                    .subjectName(subject.getName())
                    .className("Lớp: " + className)
                    .academicYear(academicYear)
                    .presentCount(present)
                    .excusedAbsentCount(excused)       // Cập nhật trường mới
                    .unexcusedAbsentCount(unexcused)   // Cập nhật trường mới
                    .totalConducted(conducted)
                    .percentage(percentage)
                    .totalYearSlots(totalYearSlots)
                    .maxUnexcusedAllowed(maxUnexcusedAllowed) // Cập nhật tên chuẩn
                    .isBannedFromExam(isBanned)
                    .build());
        }
        return result;
    }    // API 2: Màn hình Chi tiết 1 Môn

    public AttendanceDetailResponse getDetail(Long studentId, Long subjectId) {
        // 1. Lấy dữ liệu điểm danh trong QUÁ KHỨ
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
        int excused = (int) pastRecords.stream().filter(r -> r.getStatus() == AttendanceStatusEnum.EXCUSED_ABSENT).count();
        int unexcused = (int) pastRecords.stream().filter(r -> r.getStatus() == AttendanceStatusEnum.UNEXCUSED_ABSENT).count();

        int percentage = totalConducted == 0 ? 0 : Math.round(((float) present / totalConducted) * 100);

        int totalWeeksInYear = 35;
        List<ScheduleEntity> subjectSchedules = scheduleRepo.findBySchoolClassIdAndSubjectId(classId, subjectId);
        int slotsPerWeek = subjectSchedules.size();

        int totalYearSlots = slotsPerWeek * totalWeeksInYear;
        int futureCount = totalYearSlots - totalConducted;
        if (futureCount < 0) futureCount = 0;

        // Cấm thi nếu số buổi KHÔNG PHÉP vượt quá 20% tổng số tiết
        int maxUnexcusedAllowed = (int) (totalYearSlots * 0.20);
        boolean isBannedFromExam = unexcused > maxUnexcusedAllowed;

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

        Collections.reverse(recordDtos);

        // 4. TIÊN ĐOÁN TƯƠNG LAI
        if (futureCount > 0 && !subjectSchedules.isEmpty()) {
            List<AttendanceRecordDto> futureRecords = new ArrayList<>();
            LocalDate lastAttendanceDate = pastRecords.get(0).getAttendanceDate();
            LocalDate loopDate = lastAttendanceDate.plusDays(1);
            int count = futureCount;

            while (count > 0) {
                String currentDayName = loopDate.getDayOfWeek().name();

                for (ScheduleEntity schedule : subjectSchedules) {
                    if (schedule.getDayOfWeek().name().equals(currentDayName)) {
                        String teacherName = schedule.getTeacher() != null ?
                                schedule.getTeacher().getLastName() + " " + schedule.getTeacher().getFirstName() : "Chưa rõ";

                        futureRecords.add(AttendanceRecordDto.builder()
                                .date(loopDate)
                                .teacherName(teacherName.trim())
                                .period(schedule.getPeriod())
                                .status(AttendanceStatusEnum.FUTURE)
                                .build());
                        count--;
                        if (count == 0) break;
                    }
                }
                loopDate = loopDate.plusDays(1);
            }
            recordDtos.addAll(futureRecords);
        }

        // 5. Trả về cho UI
        return AttendanceDetailResponse.builder()
                .subjectName(subjectName)
                .className("Lớp: " + className)
                .percentage(percentage)
                .presentCount(present)
                .excusedAbsentCount(excused)       // Cập nhật đúng
                .unexcusedAbsentCount(unexcused)   // Cập nhật đúng
                .futureCount(futureCount)
                .totalYearSlots(totalYearSlots)
                .maxUnexcusedAllowed(maxUnexcusedAllowed) // Đã sửa lại lỗi ở đây
                .isBannedFromExam(isBannedFromExam)
                .records(recordDtos)
                .build();
    }
}
package com.borisedu.borisedu.service;

import com.borisedu.borisedu.dto.response.ExamScheduleResponse;
import com.borisedu.borisedu.entity.ExamScheduleEntity;
import com.borisedu.borisedu.entity.StudentEntity;
import com.borisedu.borisedu.exception.custom.NotFoundException;
import com.borisedu.borisedu.repository.ExamScheduleRepo;
import com.borisedu.borisedu.repository.StudentRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExamScheduleService {

    private final StudentRepo studentRepo;
    private final ExamScheduleRepo examScheduleRepo;

    public List<ExamScheduleResponse> getExamSchedules(Long studentId, String academicYearFilter) {
        StudentEntity student = studentRepo.findById(studentId)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy học sinh!"));

        if (student.getSchoolClass() == null) {
            throw new NotFoundException("Học sinh này chưa được xếp lớp!");
        }

        Long classId = student.getSchoolClass().getId();
        List<ExamScheduleEntity> exams = examScheduleRepo.findByClassId(classId);

        LocalDate today = LocalDate.now();

        return exams.stream()
                // Lọc theo năm học nếu người dùng chọn trên Dropdown (UI)
                .filter(exam -> academicYearFilter == null || exam.getSchoolClass().getAcademicYear().equals(academicYearFilter))
                .map(exam -> {
                    // Logic tính toán trạng thái
                    String status;
                    if (exam.getExamDate().isAfter(today)) {
                        status = "SẮP DIỄN RA";
                    } else if (exam.getExamDate().isBefore(today)) {
                        status = "ĐÃ KẾT THÚC";
                    } else {
                        status = "HÔM NAY";
                    }

                    return ExamScheduleResponse.builder()
                            .id(exam.getId())
                            .subjectName(exam.getSubject().getName())
                            .examType(exam.getExamType())
                            .examDate(exam.getExamDate())
                            .startTime(exam.getStartTime())
                            .endTime(exam.getEndTime())
                            .room(exam.getRoom())
                            .status(status)
                            .academicYear(exam.getSchoolClass().getAcademicYear())
                            .build();
                }).collect(Collectors.toList());
    }

}

package com.borisedu.borisedu.controller;

import com.borisedu.borisedu.dto.response.ExamScheduleResponse;
import com.borisedu.borisedu.service.ExamScheduleService;
import com.borisedu.borisedu.utils.annotation.ApiMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/exam_schedules")
@RequiredArgsConstructor
public class ExamScheduleController {

    private final ExamScheduleService examScheduleService;

    @ApiMessage("Lấy tất cả lịch thi thành công!")
    @GetMapping("{studentId}")
    public ResponseEntity<List<ExamScheduleResponse>> getExamSchedule(@PathVariable Long studentId, @RequestParam(required = false) String academicYear) {
        return ResponseEntity.ok(examScheduleService.getExamSchedules(studentId, academicYear));
    }

}

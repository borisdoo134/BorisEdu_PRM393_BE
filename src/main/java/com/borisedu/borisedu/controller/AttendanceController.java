package com.borisedu.borisedu.controller;

import com.borisedu.borisedu.dto.response.AttendanceDetailResponse;
import com.borisedu.borisedu.dto.response.AttendanceOverviewResponse;
import com.borisedu.borisedu.service.AttendanceService;
import com.borisedu.borisedu.utils.annotation.ApiMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/attendances")
@RequiredArgsConstructor
public class AttendanceController {

    private final AttendanceService attendanceService;

    @ApiMessage("Lấy báo cáo điểm danh sơ bộ thành công!")
    @GetMapping("/{studentId}")
    public ResponseEntity<List<AttendanceOverviewResponse>> getAttendanceOverview(@PathVariable Long studentId) {
        return ResponseEntity.ok(attendanceService.getOverview(studentId));
    }

    @ApiMessage("Lấy báo cáo điểm danh chi tiết thành công!")
    @GetMapping("/detail/{studentId}/subjects/{subjectId}")
    public ResponseEntity<AttendanceDetailResponse> getAttendanceOverview(@PathVariable Long studentId, @PathVariable Long subjectId) {
        return ResponseEntity.ok(attendanceService.getDetail(studentId, subjectId));
    }

}

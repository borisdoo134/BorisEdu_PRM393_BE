package com.borisedu.borisedu.controller;

import com.borisedu.borisedu.dto.response.ApiResponse;
import com.borisedu.borisedu.dto.response.ScoreDetailResponse;
import com.borisedu.borisedu.dto.response.ScoreOverviewResponse;
import com.borisedu.borisedu.service.ScoreService;
import com.borisedu.borisedu.utils.annotation.ApiMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/scores")
@RequiredArgsConstructor
public class ScoreController {

    private final ScoreService scoreService;

    // API lấy Tổng quan bảng điểm (Danh sách các môn như trên giao diện)
    @ApiMessage("Lấy Tổng quan bảng điểm thành công!")
    @GetMapping("/{studentId}")
    public ResponseEntity<List<ScoreOverviewResponse>> getScoreOverview(
            @PathVariable Long studentId,
            @RequestParam(defaultValue = "2025-2026") String academicYear,
            @RequestParam(required = false) Integer semester) {
        return ResponseEntity.ok(scoreService.getScoreOverview(studentId, academicYear, semester));
    }
    // API lấy Chi tiết bảng điểm của 1 Môn học cụ thể
    @ApiMessage("Lấy Chi tiết bảng điểm của 1 Môn học cụ thể thành công!")
    @GetMapping("/{studentId}/subjects/{subjectId}")
    public ResponseEntity<ScoreDetailResponse> getScoreDetail(
            @PathVariable Long studentId,
            @PathVariable Long subjectId,
            @RequestParam(defaultValue = "2025-2026") String academicYear,
            @RequestParam(required = false) Integer semester) {

        return ResponseEntity.ok(scoreService.getScoreDetail(studentId, subjectId, academicYear, semester));
    }
}
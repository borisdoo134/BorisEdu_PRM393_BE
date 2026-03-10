package com.borisedu.borisedu.controller;

import com.borisedu.borisedu.dto.response.ScheduleResponse;
import com.borisedu.borisedu.entity.ScheduleEntity;
import com.borisedu.borisedu.service.ScheduleService;
import com.borisedu.borisedu.utils.annotation.ApiMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/schedules")
@RequiredArgsConstructor
public class ScheduleController {

    private final ScheduleService scheduleService;

    @ApiMessage("Lấy lịch học thành công!")
    @GetMapping("/timetable/{studentId}")
    public ResponseEntity<List<ScheduleResponse>> getTimetableByStudentId(@PathVariable Long studentId) {
        return ResponseEntity.ok(scheduleService.getTimetableByStudentId(studentId));
    }

}

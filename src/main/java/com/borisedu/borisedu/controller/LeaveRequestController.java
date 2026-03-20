package com.borisedu.borisedu.controller;

import com.borisedu.borisedu.dto.request.LeaveCreationRequest;
import com.borisedu.borisedu.dto.response.LeaveRequestHistoryResponse;
import com.borisedu.borisedu.service.LeaveRequestService;
import com.borisedu.borisedu.utils.annotation.ApiMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/leave-requests")
@RequiredArgsConstructor
public class LeaveRequestController {

    private final LeaveRequestService leaveRequestService;

    @ApiMessage("Xin nghỉ học cho con thành công!")
    @PostMapping("/parents/{parentId}")
    public ResponseEntity<Void> createLeaveRequest(
            @PathVariable Long parentId,
            @RequestBody LeaveCreationRequest dto) {


        return ResponseEntity.ok(leaveRequestService.createLeaveRequest(parentId, dto));

    }

    @ApiMessage("Xem lịch sử xin nghỉ học cho con thành công!")
    @GetMapping("/parents/{parentId}/students/{studentId}")
    public ResponseEntity<List<LeaveRequestHistoryResponse>> getLeaveRequestHistory(
            @PathVariable Long parentId,
            @PathVariable Long studentId) {

        return ResponseEntity.ok(leaveRequestService.getLeaveRequestHistory(parentId, studentId));
    }
}
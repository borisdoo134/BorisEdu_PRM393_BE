package com.borisedu.borisedu.service;

import com.borisedu.borisedu.dto.request.LeaveCreationRequest;
import com.borisedu.borisedu.dto.response.LeaveRequestHistoryResponse;
import com.borisedu.borisedu.entity.LeaveRequestEntity;
import com.borisedu.borisedu.entity.UserEntity;
import com.borisedu.borisedu.repository.LeaveRequestRepo;
import com.borisedu.borisedu.repository.UserRepo;
import com.borisedu.borisedu.utils.enums.LeaveRequestStatusEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LeaveRequestService {

    private final LeaveRequestRepo leaveRequestRepo;
    private final UserRepo userRepo;

    public Void createLeaveRequest(Long parentId, LeaveCreationRequest dto) {
        // 1. Tìm người tạo đơn (Phụ huynh)
        UserEntity parent = userRepo.findById(parentId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng!"));

        // 2. CHẶN ROLE: Kiểm tra xem người này có đúng là PARENT không
        boolean isParent = parent.getRoles().stream()
                .anyMatch(role -> role.getName().name().equals("PARENT"));
        if (!isParent) {
            throw new RuntimeException("Bạn không có quyền! Chỉ Phụ huynh mới được tạo đơn xin nghỉ học.");
        }

        // 3. Tìm học sinh được xin nghỉ
        UserEntity student = userRepo.findById(dto.getStudentId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hồ sơ học sinh!"));

        // 4. CHẶN QUYỀN LÀM CHA MẸ: Đảm bảo chỉ được xin nghỉ cho con của chính mình
        if (student.getParent() == null || !student.getParent().getId().equals(parentId)) {
            throw new RuntimeException("Lỗi! Bạn chỉ có thể làm đơn xin nghỉ học cho con của mình.");
        }

        // 5. Kiểm tra logic ngày tháng hợp lệ
        if (dto.getFromDate().isAfter(dto.getToDate())) {
            throw new RuntimeException("Ngày bắt đầu không được lớn hơn ngày kết thúc!");
        }

        // 6. Lưu vào Database với trạng thái mặc định là PENDING
        LeaveRequestEntity request = LeaveRequestEntity.builder()
                .parent(parent)
                .student(student)
                .fromDate(dto.getFromDate())
                .toDate(dto.getToDate())
                .reason(dto.getReason())
                .status(LeaveRequestStatusEnum.PENDING) // <--- Ép cứng trạng thái ở đây
                .build();

        leaveRequestRepo.save(request);
        return null;
    }

    public List<LeaveRequestHistoryResponse> getLeaveRequestHistory(Long parentId, Long studentId) {
        // Lấy danh sách Đơn xin nghỉ của đích danh bé này do đúng phụ huynh này tạo
        List<LeaveRequestEntity> requests = leaveRequestRepo
                .findByParentIdAndStudentIdOrderByCreatedAtDesc(parentId, studentId);

        // Map sang DTO tinh gọn
        return requests.stream().map(req -> LeaveRequestHistoryResponse.builder()
                .id(req.getId())
                .fromDate(req.getFromDate())
                .toDate(req.getToDate())
                .reason(req.getReason())
                .status(req.getStatus())
                .createdAt(req.getCreatedAt())
                .teacherNote(req.getTeacherNote()) // Lấy lời nhắn của giáo viên
                .build()
        ).collect(Collectors.toList());
    }
}
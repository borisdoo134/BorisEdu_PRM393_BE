package com.borisedu.borisedu.dto.response;

import com.borisedu.borisedu.utils.enums.GenderEnum;
import com.borisedu.borisedu.utils.enums.StatusEnum;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserResponse {

    // --- THÔNG TIN CƠ BẢN (Ai cũng có) ---
    Long id;
    String username;
    String email;
    String phone;
    String refreshToken;
    Set<RoleResponse> roles;
    String firstName;
    String middleName;
    String lastName;
    String avatarUrl;

    // CÁC TRƯỜNG DÀNH RIÊNG CHO HỌC SINH
    // (Sẽ tự động null nếu người đăng nhập là Phụ huynh/Giáo viên)
    Instant dateOfBirth;
    String address;
    GenderEnum gender;
    StatusEnum status;
    String fatherName;
    String motherName;

    ClassResponse schoolClass;

    Set<UserResponse> children;

}
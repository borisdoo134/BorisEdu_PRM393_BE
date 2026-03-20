package com.borisedu.borisedu.entity;

import com.borisedu.borisedu.utils.enums.LeaveRequestStatusEnum;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "leave_requests")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LeaveRequestEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    // Phụ huynh làm đơn
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id", nullable = false)
    UserEntity parent;

    // Xin nghỉ cho học sinh nào
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    UserEntity student;

    @Column(nullable = false)
    LocalDate fromDate;

    @Column(nullable = false)
    LocalDate toDate;

    @Column(nullable = false, columnDefinition = "TEXT")
    String reason;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    LeaveRequestStatusEnum status;

    LocalDateTime createdAt;

    @Column(columnDefinition = "TEXT")
    String teacherNote;

    // Tự động lưu thời gian tạo đơn
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
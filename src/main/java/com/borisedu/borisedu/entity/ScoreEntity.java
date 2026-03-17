package com.borisedu.borisedu.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Entity
@Table(name = "scores")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ScoreEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    // 1. Điểm của ai? (Trỏ về bảng users, vì Học sinh giờ là UserEntity)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    UserEntity student;

    // 2. Điểm môn gì?
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subject_id", nullable = false)
    SubjectEntity subject;

    // 3. Thuộc loại điểm nào? (Hệ số mấy?)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "score_type_id", nullable = false)
    ScoreTypeEntity scoreType;

    // 4. Giá trị con điểm (VD: 8.5, 9.0)
    // Dùng Double để hỗ trợ điểm thập phân
    @Column(nullable = false)
    Double scoreValue;

    // 5. Học kỳ mấy? (1 hoặc 2)
    @Column(nullable = false)
    Integer semester;

    // 6. Năm học nào? (VD: "2025-2026")
    @Column(nullable = false)
    String academicYear;

    // 7. Ghi chú thêm (VD: "Điểm miệng bài 1", "Kiểm tra 15p thực hành")
    String description;

    // 8. Ngày nhập điểm
    LocalDate entryDate;
}
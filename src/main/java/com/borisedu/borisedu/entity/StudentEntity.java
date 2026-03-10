package com.borisedu.borisedu.entity;

import com.borisedu.borisedu.utils.enums.GenderEnum;
import com.borisedu.borisedu.utils.enums.StatusEnum;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "students")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StudentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "first-name")
    String firstName;

    @Column(name = "middle-name")
    String middleName;

    @Column(name = "last-name")
    String lastName;

    @Column(name = "class-name")
    String className; // Ví dụ: Lớp 5A

    @Column(name = "school-name")
    String schoolName; // Ví dụ: TH Chu Văn An

    @Column(name = "date-of-birth")
    Instant dateOfBirth; // Ngày sinh (25/02/2026)

    String address; // Địa chỉ (Hà Nội)

    @Enumerated(EnumType.STRING)
    GenderEnum gender;

    String phone;

    @Column(name = "father-name")
    String fatherName;

    @Column(name = "mother-name")
    String motherName;

    @Enumerated(EnumType.STRING)
    StatusEnum status;

    @Column(name = "avatar-url")
    String avatarUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id") // Tạo thẳng 1 cột parent_id trong bảng students
    UserEntity parent;

}
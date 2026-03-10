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

    @Column(name = "first_name")
    String firstName;

    @Column(name = "middle_name")
    String middleName;

    @Column(name = "last_name")
    String lastName;

    @Column(name = "date_of_birth")
    Instant dateOfBirth;

    String address;

    @Enumerated(EnumType.STRING)
    GenderEnum gender;

    String phone;

    @Column(name = "father_name")
    String fatherName;

    @Column(name = "mother_name")
    String motherName;

    @Enumerated(EnumType.STRING)
    StatusEnum status;

    @Column(name = "avatar_url")
    String avatarUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id") // Tạo thẳng 1 cột parent_id trong bảng students
    UserEntity parent;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "class_id") // Tạo cột class_id trong bảng students
    ClassEntity schoolClass;

}
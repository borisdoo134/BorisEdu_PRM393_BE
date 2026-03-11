package com.borisedu.borisedu.entity;

import com.borisedu.borisedu.utils.enums.GenderEnum;
import com.borisedu.borisedu.utils.enums.StatusEnum;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(nullable = false, unique = true)
    String username;

    @Column(nullable = false)
    String password;

    @Column(unique = true)
    String email;

    @Column(name = "first_name")
    String firstName;

    @Column(name = "middle_name")
    String middleName;

    @Column(name = "last_name")
    String lastName;

    @Column(name = "phone", length = 15, unique = true)
    String phone;

    @Column(name = "avatar_url")
    String avatarUrl;

    @Column(name = "refresh_token")
    String refreshToken;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_role", // Tên bảng trung gian trong database
            joinColumns = @JoinColumn(name = "user_id"), // Khóa ngoại trỏ đến bảng users
            inverseJoinColumns = @JoinColumn(name = "role_id") // Khóa ngoại trỏ đến bảng roles
    )
    Set<RoleEntity> roles = new HashSet<>();

    Instant dateOfBirth;

    String address;

    @Enumerated(EnumType.STRING)
    GenderEnum gender;

    @Enumerated(EnumType.STRING)
    StatusEnum status;

    String fatherName;
    String motherName;

    // Học sinh thì thuộc về Lớp nào?
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "class_id")
    ClassEntity schoolClass;

    // QUAN HỆ TỰ CHIẾU (SELF-REFERENCING)

    // 1. Phụ huynh của user này là ai? (Dành cho Học sinh)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    UserEntity parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    Set<UserEntity> children = new HashSet<>();
}
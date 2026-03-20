package com.borisedu.borisedu.entity;
import com.borisedu.borisedu.utils.enums.RoleEnum;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "roles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RoleEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_id")
    Long id;

    @Column(nullable = false, unique = true)
    @Enumerated(EnumType.STRING)
    RoleEnum name;

    @ManyToMany(mappedBy = "roles")
    Set<UserEntity> users = new HashSet<>();
}
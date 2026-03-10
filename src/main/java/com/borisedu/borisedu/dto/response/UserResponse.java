package com.borisedu.borisedu.dto.response;


import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Set;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserResponse {
    Long id;

    String username;

    String email;

    String phone;

    String refreshToken;

    Set<RoleResponse> roles;

    String firstName;

    String middleName;

    String lastName;

    Set<StudentResponse> students;


}

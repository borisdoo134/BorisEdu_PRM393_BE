package com.borisedu.borisedu.dto.response;

import com.borisedu.borisedu.utils.enums.GenderEnum;
import com.borisedu.borisedu.utils.enums.StatusEnum;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import java.time.Instant;


@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StudentResponse {

    Long id;

    String firstName;

    String middleName;

    String lastName;

    Instant dateOfBirth;

    String address; // Địa chỉ (Hà Nội)

    @Enumerated(EnumType.STRING)
    GenderEnum gender;

    String phone;

    String fatherName;

    String motherName;

    @Enumerated(EnumType.STRING)
    StatusEnum status;

    String avatarUrl;

    ClassResponse schoolClass;

}

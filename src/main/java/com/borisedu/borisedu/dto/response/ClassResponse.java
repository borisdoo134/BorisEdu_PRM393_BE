package com.borisedu.borisedu.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ClassResponse {

    Long id;

    String className;

    String schoolName;

    String academicYear;

}

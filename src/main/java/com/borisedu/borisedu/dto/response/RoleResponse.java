package com.borisedu.borisedu.dto.response;

import com.borisedu.borisedu.utils.enums.RoleEnum;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RoleResponse {

    private Long id;

    private RoleEnum name;

}

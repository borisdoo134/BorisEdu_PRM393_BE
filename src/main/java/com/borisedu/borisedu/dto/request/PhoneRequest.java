package com.borisedu.borisedu.dto.request;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PhoneRequest {

    @Pattern(
            regexp = "^(0|\\+84|84)[35789]\\d{8}$",
            message = "Số điện thoại không đúng định dạng!"
    )
    @NotBlank(message = "Số điện thoại không được bỏ trống!")
    String phone;

}

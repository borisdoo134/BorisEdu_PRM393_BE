package com.borisedu.borisedu.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDate;

@Data
@Builder
public class ScoreRecordDto {
    private Long id;
    private String scoreTypeName; // VD: "Đánh giá thường xuyên"
    private String scoreTypeCode; // VD: "DGTX" (Để Flutter phân loại màu sắc nếu cần)
    private Integer coefficient;  // Hệ số: 1, 2, 3
    private Double scoreValue;    // Giá trị điểm: 8.5
    private String description;   // Ghi chú (nếu có)
    private LocalDate entryDate;  // Ngày nhập điểm

    private Integer semester;
}
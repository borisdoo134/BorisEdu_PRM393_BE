package com.borisedu.borisedu.dto.response;
import com.borisedu.borisedu.dto.ScoreRecordDto;



import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ScoreDetailResponse {
    private String subjectName;
    private Double averageScore; // Điểm trung bình của riêng môn này

    // Danh sách chi tiết các con điểm của môn học
    private List<ScoreRecordDto> records;
}
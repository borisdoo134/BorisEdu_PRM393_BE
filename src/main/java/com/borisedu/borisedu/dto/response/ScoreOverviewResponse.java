package com.borisedu.borisedu.dto.response;


import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ScoreOverviewResponse {
    Long subjectId;
    String subjectName;
    String className;

    Double averageScore;
}
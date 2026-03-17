package com.borisedu.borisedu.service;

import com.borisedu.borisedu.dto.ScoreRecordDto;
import com.borisedu.borisedu.dto.response.ScoreDetailResponse;
import com.borisedu.borisedu.dto.response.ScoreOverviewResponse;
import com.borisedu.borisedu.entity.ScoreEntity;
import com.borisedu.borisedu.entity.SubjectEntity;
import com.borisedu.borisedu.entity.UserEntity;
import com.borisedu.borisedu.repository.ScoreRepo;
import com.borisedu.borisedu.repository.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ScoreService {

    private final ScoreRepo scoreRepo;
    private final UserRepo userRepo;

    public List<ScoreOverviewResponse> getScoreOverview(Long studentId, String academicYear, Integer semester) {
        UserEntity student = userRepo.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy học sinh!"));

        String className = student.getSchoolClass() != null ? student.getSchoolClass().getClassName() : "Chưa xếp lớp";

        // 1. Lấy dữ liệu điểm tùy theo dropdown Học kỳ (1, 2, hoặc Cả năm = null)
        List<ScoreEntity> scores;
        if (semester != null && semester > 0) {
            scores = scoreRepo.findByStudentIdAndAcademicYearAndSemester(studentId, academicYear, semester);
        } else {
            scores = scoreRepo.findByStudentIdAndAcademicYear(studentId, academicYear);
        }

        // 2. Gom nhóm các con điểm theo từng Môn học
        Map<SubjectEntity, List<ScoreEntity>> scoresBySubject = scores.stream()
                .collect(Collectors.groupingBy(ScoreEntity::getSubject));

        List<ScoreOverviewResponse> result = new ArrayList<>();

        for (Map.Entry<SubjectEntity, List<ScoreEntity>> entry : scoresBySubject.entrySet()) {
            SubjectEntity subject = entry.getKey();
            List<ScoreEntity> subjectScores = entry.getValue();

            Double finalAvg = null;

            // 3. Tính toán tùy theo Học kỳ hay Cả năm
            if (semester != null && semester > 0) {
                // Tính trung bình 1 học kỳ
                finalAvg = calculateAverage(subjectScores);
            } else {
                // Tính trung bình CẢ NĂM: (TBHK1 + TBHK2 * 2) / 3
                List<ScoreEntity> hk1Scores = subjectScores.stream().filter(s -> s.getSemester() == 1).collect(Collectors.toList());
                List<ScoreEntity> hk2Scores = subjectScores.stream().filter(s -> s.getSemester() == 2).collect(Collectors.toList());

                Double avgHk1 = calculateAverage(hk1Scores);
                Double avgHk2 = calculateAverage(hk2Scores);

                if (avgHk1 != null && avgHk2 != null) {
                    finalAvg = (avgHk1 + avgHk2 * 2) / 3.0;
                } else if (avgHk1 != null) {
                    finalAvg = avgHk1; // Nếu chưa học HK2 thì tạm lấy HK1
                } else if (avgHk2 != null) {
                    finalAvg = avgHk2;
                }
            }

            // Làm tròn 1 chữ số thập phân (Ví dụ: 8.54 -> 8.5)
            if (finalAvg != null) {
                finalAvg = (double) Math.round(finalAvg * 10) / 10.0;
            }

            result.add(ScoreOverviewResponse.builder()
                    .subjectId(subject.getId())
                    .subjectName(subject.getName())
                    .className("Lớp: " + className)
                    .averageScore(finalAvg)
                    .build());
        }

        return result;
    }

    // Hàm phụ trợ: Tính trung bình cộng có trọng số (Hệ số 1, 2, 3)
    private Double calculateAverage(List<ScoreEntity> scores) {
        if (scores == null || scores.isEmpty()) return null;

        double totalPoints = 0.0;
        int totalCoefficients = 0;

        for (ScoreEntity s : scores) {
            int coeff = s.getScoreType().getCoefficient();
            totalPoints += (s.getScoreValue() * coeff);
            totalCoefficients += coeff;
        }

        return totalCoefficients == 0 ? null : totalPoints / totalCoefficients;
    }

    public ScoreDetailResponse getScoreDetail(Long studentId, Long subjectId, String academicYear, Integer semester) {
        // 1. Lấy danh sách các con điểm
        List<ScoreEntity> subjectScores;
        if (semester != null && semester > 0) {
            subjectScores = scoreRepo.findByStudentIdAndSubjectIdAndAcademicYearAndSemester(studentId, subjectId, academicYear, semester);
        } else {
            subjectScores = scoreRepo.findByStudentIdAndSubjectIdAndAcademicYear(studentId, subjectId, academicYear);
        }

        if (subjectScores.isEmpty()) {
            throw new RuntimeException("Không có dữ liệu điểm cho môn học này!");
        }

        String subjectName = subjectScores.get(0).getSubject().getName();
        Double finalAvg = null;

        // 2. Tính điểm trung bình (Tương tự logic ở API Tổng quan)
        if (semester != null && semester > 0) {
            finalAvg = calculateAverage(subjectScores);
        } else {
            List<ScoreEntity> hk1Scores = subjectScores.stream().filter(s -> s.getSemester() == 1).collect(Collectors.toList());
            List<ScoreEntity> hk2Scores = subjectScores.stream().filter(s -> s.getSemester() == 2).collect(Collectors.toList());

            Double avgHk1 = calculateAverage(hk1Scores);
            Double avgHk2 = calculateAverage(hk2Scores);

            if (avgHk1 != null && avgHk2 != null) {
                finalAvg = (avgHk1 + avgHk2 * 2) / 3.0;
            } else if (avgHk1 != null) {
                finalAvg = avgHk1;
            } else if (avgHk2 != null) {
                finalAvg = avgHk2;
            }
        }

        if (finalAvg != null) {
            finalAvg = (double) Math.round(finalAvg * 10) / 10.0;
        }

        // 3. Map chi tiết điểm sang DTO để trả về
        List<ScoreRecordDto> recordDtos = subjectScores.stream().map(score -> ScoreRecordDto.builder()
                .id(score.getId())
                .scoreTypeName(score.getScoreType().getName())
                .scoreTypeCode(score.getScoreType().getCode())
                .coefficient(score.getScoreType().getCoefficient())
                .scoreValue(score.getScoreValue())
                .description(score.getDescription())
                .entryDate(score.getEntryDate())
                .semester(score.getSemester()) // THÊM DÒNG NÀY
                .build()
        ).collect(Collectors.toList());

        return ScoreDetailResponse.builder()
                .subjectName(subjectName)
                .averageScore(finalAvg)
                .records(recordDtos)
                .build();
    }
}

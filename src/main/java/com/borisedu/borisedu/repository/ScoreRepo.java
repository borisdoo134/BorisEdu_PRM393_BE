package com.borisedu.borisedu.repository;

import com.borisedu.borisedu.entity.ScoreEntity;
import com.borisedu.borisedu.repository.custom.JpaSpecificationRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScoreRepo extends JpaSpecificationRepository<ScoreEntity, Long> {
    List<ScoreEntity> findByStudentIdAndAcademicYearAndSemester(Long studentId, String academicYear, Integer semester);
    List<ScoreEntity> findByStudentIdAndAcademicYear(Long studentId, String academicYear);
    // Lấy chi tiết điểm của 1 MÔN trong 1 HỌC KỲ
    List<ScoreEntity> findByStudentIdAndSubjectIdAndAcademicYearAndSemester(
            Long studentId, Long subjectId, String academicYear, Integer semester);

    // Lấy chi tiết điểm của 1 MÔN trong CẢ NĂM
    List<ScoreEntity> findByStudentIdAndSubjectIdAndAcademicYear(
            Long studentId, Long subjectId, String academicYear);
}

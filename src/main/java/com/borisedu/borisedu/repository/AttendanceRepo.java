package com.borisedu.borisedu.repository;

import com.borisedu.borisedu.entity.AttendanceEntity;
import com.borisedu.borisedu.repository.custom.JpaSpecificationRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AttendanceRepo extends JpaSpecificationRepository<AttendanceEntity, Long> {
    @Query("SELECT a FROM AttendanceEntity a JOIN FETCH a.schedule s JOIN FETCH s.subject JOIN FETCH s.schoolClass LEFT JOIN FETCH s.teacher WHERE a.student.id = :studentId")
    List<AttendanceEntity> findByStudentId(@Param("studentId") Long studentId);

    // Lấy điểm danh của 1 học sinh theo môn học cụ thể, sắp xếp ngày mới nhất lên đầu
    @Query("SELECT a FROM AttendanceEntity a JOIN FETCH a.schedule s JOIN FETCH s.subject JOIN FETCH s.schoolClass LEFT JOIN FETCH s.teacher WHERE a.student.id = :studentId AND s.subject.id = :subjectId ORDER BY a.attendanceDate DESC")
    List<AttendanceEntity> findByStudentIdAndSubjectId(@Param("studentId") Long studentId, @Param("subjectId") Long subjectId);
}

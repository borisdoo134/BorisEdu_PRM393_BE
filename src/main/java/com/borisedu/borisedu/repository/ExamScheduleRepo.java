package com.borisedu.borisedu.repository;

import com.borisedu.borisedu.entity.ExamScheduleEntity;
import com.borisedu.borisedu.repository.custom.JpaSpecificationRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExamScheduleRepo extends JpaSpecificationRepository<ExamScheduleEntity, Long> {
    @Query("SELECT e FROM ExamScheduleEntity e JOIN FETCH e.subject JOIN FETCH e.schoolClass WHERE e.schoolClass.id = :classId ORDER BY e.examDate ASC, e.startTime ASC")
    List<ExamScheduleEntity> findByClassId(@Param("classId") Long classId);
}


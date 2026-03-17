package com.borisedu.borisedu.repository;

import com.borisedu.borisedu.entity.ScheduleEntity;
import com.borisedu.borisedu.repository.custom.JpaSpecificationRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScheduleRepo extends JpaSpecificationRepository<ScheduleEntity, Long> {
    @Query("SELECT s FROM ScheduleEntity s JOIN FETCH s.subject WHERE s.schoolClass.id = :classId ORDER BY s.dayOfWeek ASC, s.period ASC")
    List<ScheduleEntity> findByClassId(@Param("classId") Long classId);
    int countBySchoolClassIdAndSubjectId(Long classId, Long subjectId);
    List<ScheduleEntity> findBySchoolClassIdAndSubjectId(Long classId, Long subjectId);
}

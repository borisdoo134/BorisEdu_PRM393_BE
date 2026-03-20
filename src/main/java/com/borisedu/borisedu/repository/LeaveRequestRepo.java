package com.borisedu.borisedu.repository;

import com.borisedu.borisedu.entity.LeaveRequestEntity;
import com.borisedu.borisedu.repository.custom.JpaSpecificationRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LeaveRequestRepo extends JpaSpecificationRepository<LeaveRequestEntity, Long> {
    List<LeaveRequestEntity> findByParentIdAndStudentIdOrderByCreatedAtDesc(Long parentId, Long studentId);
}
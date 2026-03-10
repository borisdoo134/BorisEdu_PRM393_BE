package com.borisedu.borisedu.repository;

import com.borisedu.borisedu.entity.StudentEntity;
import com.borisedu.borisedu.repository.custom.JpaSpecificationRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentRepo extends JpaSpecificationRepository<StudentEntity, Long> {
}

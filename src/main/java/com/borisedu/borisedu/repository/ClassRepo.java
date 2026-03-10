package com.borisedu.borisedu.repository;

import com.borisedu.borisedu.entity.ClassEntity;
import com.borisedu.borisedu.repository.custom.JpaSpecificationRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClassRepo extends JpaSpecificationRepository<ClassEntity, Long> {
}

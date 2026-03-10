package com.borisedu.borisedu.repository;

import com.borisedu.borisedu.entity.SubjectEntity;
import com.borisedu.borisedu.repository.custom.JpaSpecificationRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SubjectRepo extends JpaSpecificationRepository<SubjectEntity, Long> {
}

package com.borisedu.borisedu.repository;

import com.borisedu.borisedu.entity.RoleEntity;
import com.borisedu.borisedu.repository.custom.JpaSpecificationRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepo extends JpaSpecificationRepository<RoleEntity, Long> {
}

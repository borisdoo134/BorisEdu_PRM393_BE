package com.borisedu.borisedu.repository;

import com.borisedu.borisedu.entity.RoleEntity;
import com.borisedu.borisedu.repository.custom.JpaSpecificationRepository;
import com.borisedu.borisedu.utils.enums.RoleEnum;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepo extends JpaSpecificationRepository<RoleEntity, Long> {
    Optional<RoleEntity> findByName(RoleEnum name);
}

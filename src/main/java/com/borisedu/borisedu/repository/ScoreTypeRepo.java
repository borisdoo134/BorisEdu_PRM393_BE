package com.borisedu.borisedu.repository;

import com.borisedu.borisedu.entity.ScoreTypeEntity;
import com.borisedu.borisedu.repository.custom.JpaSpecificationRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ScoreTypeRepo extends JpaSpecificationRepository<ScoreTypeEntity, Long> {
    Optional<ScoreTypeEntity> findByCode(String code);
}
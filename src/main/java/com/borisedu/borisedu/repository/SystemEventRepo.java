package com.borisedu.borisedu.repository;

import com.borisedu.borisedu.entity.SystemEventEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SystemEventRepo extends JpaRepository<SystemEventEntity, Long> {
    List<SystemEventEntity> findByActiveTrueOrderByCreatedAtDesc();
}
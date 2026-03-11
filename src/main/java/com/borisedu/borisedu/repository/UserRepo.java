package com.borisedu.borisedu.repository;


import com.borisedu.borisedu.entity.UserEntity;
import com.borisedu.borisedu.repository.custom.JpaSpecificationRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepo extends JpaSpecificationRepository<UserEntity, Long> {

    @EntityGraph(attributePaths = {"children", "children.schoolClass"})
    Optional<UserEntity> findByPhone(String phone);
}
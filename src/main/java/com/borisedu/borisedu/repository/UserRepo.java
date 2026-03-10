package com.borisedu.borisedu.repository;


import com.borisedu.borisedu.entity.UserEntity;
import com.borisedu.borisedu.repository.custom.JpaSpecificationRepository;

import java.util.Optional;

public interface UserRepo extends JpaSpecificationRepository<UserEntity, Long> {
    Optional<UserEntity> findByPhone(String phone);
}

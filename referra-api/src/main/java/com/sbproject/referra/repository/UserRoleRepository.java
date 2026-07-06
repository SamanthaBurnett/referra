package com.sbproject.referra.repository;

import com.sbproject.referra.model.User;
import com.sbproject.referra.model.UserRole;
import com.sbproject.referra.model.UserRoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface UserRoleRepository extends JpaRepository<UserRoleEntity, UUID> {

    List<UserRoleEntity> findByUser(User user);

    boolean existsByUserAndRole(User user, UserRole role);
}

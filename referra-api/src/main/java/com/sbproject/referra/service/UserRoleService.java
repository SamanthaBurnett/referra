package com.sbproject.referra.service;

import com.sbproject.referra.model.User;
import com.sbproject.referra.model.UserRole;
import com.sbproject.referra.model.UserRoleEntity;
import com.sbproject.referra.repository.UserRoleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserRoleService {

    private final UserRoleRepository userRoleRepository;

    public UserRoleService(UserRoleRepository userRoleRepository) {
        this.userRoleRepository = userRoleRepository;
    }

    @Transactional(readOnly = true)
    public Set<UserRole> getRoles(User user) {
        return userRoleRepository.findByUser(user)
                .stream()
                .map(UserRoleEntity::getRole)
                .collect(Collectors.toSet());
    }

    @Transactional
    public Set<UserRole> addRole(User user, UserRole requestedRole) {

        Set<UserRole> existingRoles = getRoles(user);

        if (!existingRoles.isEmpty()) {
            throw new IllegalStateException(
                    "A role has already been assigned to this user."
            );
        }

        UserRoleEntity userRole = UserRoleEntity.builder()
                .user(user)
                .role(requestedRole)
                .createdAt(Instant.now())
                .build();

        userRoleRepository.save(userRole);

        return getRoles(user);
    }
}

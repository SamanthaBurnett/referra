package com.sbproject.referra.service;

import com.sbproject.referra.dto.UserResponse;
import com.sbproject.referra.model.User;
import com.sbproject.referra.model.UserRole;
import com.sbproject.referra.model.UserStatus;
import com.sbproject.referra.repository.UserRepository;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Set;

@Service
public class CurrentUserService {

    private final UserRepository userRepository;
    private final UserRoleService userRoleService;

    public CurrentUserService(
            UserRepository userRepository,
            UserRoleService userRoleService
    ) {
        this.userRepository = userRepository;
        this.userRoleService = userRoleService;
    }

    @Transactional
    public User getOrCreateUser(Jwt jwt) {
        String cognitoSub = jwt.getSubject();
        String email = jwt.getClaimAsString("email");

        return userRepository.findByCognitoSub(cognitoSub)
                .orElseGet(() -> createUser(cognitoSub, email));
    }

    @Transactional
    public UserResponse getCurrentUserResponse(Jwt jwt) {
        User user = getOrCreateUser(jwt);
        Set<UserRole> roles = userRoleService.getRoles(user);

        return UserResponse.from(user, roles);
    }

    @Transactional
    public UserResponse addRole(Jwt jwt, UserRole role) {
        User user = getOrCreateUser(jwt);
        Set<UserRole> roles = userRoleService.addRole(user, role);

        return UserResponse.from(user, roles);
    }

    private User createUser(String cognitoSub, String email) {
        Instant now = Instant.now();

        User user = User.builder()
                .cognitoSub(cognitoSub)
                .email(email)
                .status(UserStatus.ACTIVE)
                .createdAt(now)
                .updatedAt(now)
                .build();

        return userRepository.save(user);
    }
}

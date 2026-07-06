package com.sbproject.referra.service;

import com.sbproject.referra.model.User;
import com.sbproject.referra.model.UserStatus;
import com.sbproject.referra.repository.UserRepository;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
public class CurrentUserService {

    private final UserRepository userRepository;

    public CurrentUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public User getOrCreateUser(Jwt jwt) {
        String cognitoSub = jwt.getSubject();
        String email = jwt.getClaimAsString("email");

        return userRepository.findByCognitoSub(cognitoSub)
                .orElseGet(() -> createUser(cognitoSub, email));
    }

    private User createUser(String cognitoSub, String email) {
        Instant now = Instant.now();

        User user = new User();
        user.setCognitoSub(cognitoSub);
        user.setEmail(email);
        user.setStatus(UserStatus.ACTIVE);
        user.setCreatedAt(now);
        user.setUpdatedAt(now);

        return userRepository.save(user);
    }
}

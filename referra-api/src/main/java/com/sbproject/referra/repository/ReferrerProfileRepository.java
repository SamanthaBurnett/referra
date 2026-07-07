package com.sbproject.referra.repository;

import com.sbproject.referra.model.ReferrerProfile;
import com.sbproject.referra.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ReferrerProfileRepository extends JpaRepository<ReferrerProfile, UUID> {

    Optional<ReferrerProfile> findByUser(User user);

    boolean existsByUser(User user);
}

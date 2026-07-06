package com.sbproject.referra.repository;

import com.sbproject.referra.model.CandidateProfile;
import com.sbproject.referra.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CandidateProfileRepository extends JpaRepository<CandidateProfile, UUID> {

    Optional<CandidateProfile> findByUser(User user);

    boolean existsByUser(User user);
}

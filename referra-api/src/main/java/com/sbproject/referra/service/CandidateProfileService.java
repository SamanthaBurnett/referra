package com.sbproject.referra.service;

import com.sbproject.referra.dto.CandidateProfileResponse;
import com.sbproject.referra.dto.CreateCandidateProfileRequest;
import com.sbproject.referra.dto.UpdateCandidateProfileRequest;
import com.sbproject.referra.model.CandidateProfile;
import com.sbproject.referra.model.User;
import com.sbproject.referra.model.UserRole;
import com.sbproject.referra.repository.CandidateProfileRepository;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Set;

@Service
public class CandidateProfileService {

    private final CurrentUserService currentUserService;
    private final UserRoleService userRoleService;
    private final CandidateProfileRepository candidateProfileRepository;

    public CandidateProfileService(
            CurrentUserService currentUserService,
            UserRoleService userRoleService,
            CandidateProfileRepository candidateProfileRepository
    ) {
        this.currentUserService = currentUserService;
        this.userRoleService = userRoleService;
        this.candidateProfileRepository = candidateProfileRepository;
    }

    @Transactional
    public CandidateProfileResponse createProfile(Jwt jwt, CreateCandidateProfileRequest request) {
        User user = currentUserService.getOrCreateUser(jwt);

        requireCandidateRole(user);

        if (candidateProfileRepository.existsByUser(user)) {
            throw new IllegalStateException("Candidate profile already exists for this user.");
        }

        Instant now = Instant.now();

        CandidateProfile profile = CandidateProfile.builder()
                .user(user)
                .firstName(request.firstName())
                .lastName(request.lastName())
                .headline(request.headline())
                .skills(toArray(request.skills()))
                .targetRoles(toArray(request.targetRoles()))
                .linkedinUrl(request.linkedinUrl())
                .githubUrl(request.githubUrl())
                .portfolioUrl(request.portfolioUrl())
                .hasReferencesAvailable(request.hasReferencesAvailable())
                .createdAt(now)
                .updatedAt(now)
                .build();

        CandidateProfile savedProfile = candidateProfileRepository.save(profile);

        return CandidateProfileResponse.from(savedProfile);
    }

    @Transactional(readOnly = true)
    public CandidateProfileResponse getProfile(Jwt jwt) {
        User user = currentUserService.getOrCreateUser(jwt);

        CandidateProfile profile = candidateProfileRepository.findByUser(user)
                .orElseThrow(() -> new IllegalStateException("Candidate profile not found."));

        return CandidateProfileResponse.from(profile);
    }

    @Transactional
    public CandidateProfileResponse updateProfile(Jwt jwt, UpdateCandidateProfileRequest request) {
        User user = currentUserService.getOrCreateUser(jwt);

        requireCandidateRole(user);

        CandidateProfile profile = candidateProfileRepository.findByUser(user)
                .orElseThrow(() -> new IllegalStateException("Candidate profile not found."));

        if (request.firstName() != null) {
            profile.setFirstName(request.firstName());
        }

        if (request.lastName() != null) {
            profile.setLastName(request.lastName());
        }

        if (request.headline() != null) {
            profile.setHeadline(request.headline());
        }

        if (request.skills() != null) {
            profile.setSkills(toArray(request.skills()));
        }

        if (request.targetRoles() != null) {
            profile.setTargetRoles(toArray(request.targetRoles()));
        }

        if (request.linkedinUrl() != null) {
            profile.setLinkedinUrl(request.linkedinUrl());
        }

        if (request.githubUrl() != null) {
            profile.setGithubUrl(request.githubUrl());
        }

        if (request.portfolioUrl() != null) {
            profile.setPortfolioUrl(request.portfolioUrl());
        }

        if (request.hasReferencesAvailable() != null) {
            profile.setHasReferencesAvailable(request.hasReferencesAvailable());
        }

        profile.setUpdatedAt(Instant.now());

        return CandidateProfileResponse.from(profile);
    }

    private void requireCandidateRole(User user) {
        Set<UserRole> roles = userRoleService.getRoles(user);

        if (!roles.contains(UserRole.CANDIDATE)) {
            throw new IllegalStateException("User must have CANDIDATE role.");
        }
    }

    private String[] toArray(List<String> values) {
        if (values == null) {
            return new String[0];
        }

        return values.toArray(String[]::new);
    }
}

package com.sbproject.referra.service;

import com.sbproject.referra.dto.CreateReferrerProfileRequest;
import com.sbproject.referra.dto.ReferrerProfileResponse;
import com.sbproject.referra.dto.UpdateReferrerProfileRequest;
import com.sbproject.referra.model.Company;
import com.sbproject.referra.model.ReferrerProfile;
import com.sbproject.referra.model.User;
import com.sbproject.referra.model.UserRole;
import com.sbproject.referra.repository.ReferrerProfileRepository;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Set;

@Service
public class ReferrerProfileService {

    private static final int DEFAULT_MAX_REQUESTS_PER_WEEK = 5;

    private final CurrentUserService currentUserService;
    private final UserRoleService userRoleService;
    private final CompanyService companyService;
    private final ReferrerProfileRepository referrerProfileRepository;

    public ReferrerProfileService(
            CurrentUserService currentUserService,
            UserRoleService userRoleService,
            CompanyService companyService,
            ReferrerProfileRepository referrerProfileRepository
    ) {
        this.currentUserService = currentUserService;
        this.userRoleService = userRoleService;
        this.companyService = companyService;
        this.referrerProfileRepository = referrerProfileRepository;
    }

    @Transactional
    public ReferrerProfileResponse createProfile(Jwt jwt, CreateReferrerProfileRequest request) {
        User user = currentUserService.getOrCreateUser(jwt);

        requireReferrerRole(user);

        if (referrerProfileRepository.existsByUser(user)) {
            throw new IllegalStateException("Referrer profile already exists for this user.");
        }

        Company company = companyService.findOrCreateCompany(request.companyName());

        Instant now = Instant.now();

        ReferrerProfile profile = ReferrerProfile.builder()
                .user(user)
                .company(company)
                .jobTitle(request.jobTitle())
                .seniority(request.seniority())
                .skills(toArray(request.skills()))
                .bio(request.bio())
                .referralStatus(request.referralStatus())
                .maxRequestsPerWeek(resolveMaxRequestsPerWeek(request.maxRequestsPerWeek()))
                .lastRespondedAt(null)
                .createdAt(now)
                .updatedAt(now)
                .build();

        ReferrerProfile savedProfile = referrerProfileRepository.save(profile);

        return ReferrerProfileResponse.from(savedProfile);
    }

    @Transactional(readOnly = true)
    public ReferrerProfileResponse getProfile(Jwt jwt) {
        User user = currentUserService.getOrCreateUser(jwt);

        ReferrerProfile profile = referrerProfileRepository.findByUser(user)
                .orElseThrow(() -> new IllegalStateException("Referrer profile not found."));

        return ReferrerProfileResponse.from(profile);
    }

    @Transactional
    public ReferrerProfileResponse updateProfile(Jwt jwt, UpdateReferrerProfileRequest request) {
        User user = currentUserService.getOrCreateUser(jwt);

        requireReferrerRole(user);

        ReferrerProfile profile = referrerProfileRepository.findByUser(user)
                .orElseThrow(() -> new IllegalStateException("Referrer profile not found."));

        if (request.companyName() != null) {
            Company company = companyService.findOrCreateCompany(request.companyName());
            profile.setCompany(company);
        }

        if (request.jobTitle() != null) {
            profile.setJobTitle(request.jobTitle());
        }

        if (request.seniority() != null) {
            profile.setSeniority(request.seniority());
        }

        if (request.skills() != null) {
            profile.setSkills(toArray(request.skills()));
        }

        if (request.bio() != null) {
            profile.setBio(request.bio());
        }

        if (request.referralStatus() != null) {
            profile.setReferralStatus(request.referralStatus());
        }

        if (request.maxRequestsPerWeek() != null) {
            profile.setMaxRequestsPerWeek(resolveMaxRequestsPerWeek(request.maxRequestsPerWeek()));
        }

        profile.setUpdatedAt(Instant.now());

        return ReferrerProfileResponse.from(profile);
    }

    private void requireReferrerRole(User user) {
        Set<UserRole> roles = userRoleService.getRoles(user);

        if (!roles.contains(UserRole.REFERRER)) {
            throw new IllegalStateException("User must have REFERRER role.");
        }
    }

    private String[] toArray(List<String> values) {
        if (values == null) {
            return new String[0];
        }

        return values.toArray(String[]::new);
    }

    private int resolveMaxRequestsPerWeek(Integer maxRequestsPerWeek) {
        if (maxRequestsPerWeek == null) {
            return DEFAULT_MAX_REQUESTS_PER_WEEK;
        }

        if (maxRequestsPerWeek < 0) {
            throw new IllegalStateException("Max requests per week cannot be negative.");
        }

        return maxRequestsPerWeek;
    }
}

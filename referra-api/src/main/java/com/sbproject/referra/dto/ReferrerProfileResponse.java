package com.sbproject.referra.dto;

import com.sbproject.referra.model.ReferralStatus;
import com.sbproject.referra.model.ReferrerProfile;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public record ReferrerProfileResponse(
        UUID id,
        UUID userId,
        CompanyResponse company,
        String jobTitle,
        String seniority,
        List<String> skills,
        String bio,
        ReferralStatus referralStatus,
        Integer maxRequestsPerWeek,
        Instant lastRespondedAt
) {

    public static ReferrerProfileResponse from(ReferrerProfile profile) {
        return new ReferrerProfileResponse(
                profile.getId(),
                profile.getUser().getId(),
                CompanyResponse.from(profile.getCompany()),
                profile.getJobTitle(),
                profile.getSeniority(),
                Arrays.asList(profile.getSkills()),
                profile.getBio(),
                profile.getReferralStatus(),
                profile.getMaxRequestsPerWeek(),
                profile.getLastRespondedAt()
        );
    }
}

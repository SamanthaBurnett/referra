package com.sbproject.referra.dto;

import com.sbproject.referra.model.CandidateProfile;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public record CandidateProfileResponse(
        UUID id,
        UUID userId,
        String firstName,
        String lastName,
        String headline,
        List<String> skills,
        List<String> targetRoles,
        String linkedinUrl,
        String githubUrl,
        String portfolioUrl,
        boolean hasReferencesAvailable
) {

    public static CandidateProfileResponse from(CandidateProfile profile) {
        return new CandidateProfileResponse(
                profile.getId(),
                profile.getUser().getId(),
                profile.getFirstName(),
                profile.getLastName(),
                profile.getHeadline(),
                Arrays.asList(profile.getSkills()),
                Arrays.asList(profile.getTargetRoles()),
                profile.getLinkedinUrl(),
                profile.getGithubUrl(),
                profile.getPortfolioUrl(),
                profile.isHasReferencesAvailable()
        );
    }
}

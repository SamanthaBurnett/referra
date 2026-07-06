package com.sbproject.referra.dto;

import jakarta.validation.constraints.Size;

import java.util.List;

public record UpdateCandidateProfileRequest(

        @Size(max = 100)
        String firstName,

        @Size(max = 100)
        String lastName,

        @Size(max = 255)
        String headline,

        List<@Size(max = 100) String> skills,

        List<@Size(max = 100) String> targetRoles,

        @Size(max = 500)
        String linkedinUrl,

        @Size(max = 500)
        String githubUrl,

        @Size(max = 500)
        String portfolioUrl,

        Boolean hasReferencesAvailable
) {
}

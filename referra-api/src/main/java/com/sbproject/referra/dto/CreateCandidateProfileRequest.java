package com.sbproject.referra.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;

public record CreateCandidateProfileRequest(

        @NotBlank
        @Size(max = 100)
        String firstName,

        @NotBlank
        @Size(max = 100)
        String lastName,

        @Size(max = 255)
        String headline,

        List<@NotBlank @Size(max = 100) String> skills,

        List<@NotBlank @Size(max = 100) String> targetRoles,

        @Size(max = 500)
        String linkedinUrl,

        @Size(max = 500)
        String githubUrl,

        @Size(max = 500)
        String portfolioUrl,

        boolean hasReferencesAvailable
) {
}

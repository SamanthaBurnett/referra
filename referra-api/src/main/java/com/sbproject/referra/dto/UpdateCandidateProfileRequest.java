package com.sbproject.referra.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.URL;

import java.util.List;

public record UpdateCandidateProfileRequest(

        @Size(max = 100)
        String firstName,

        @Size(max = 100)
        String lastName,

        @Size(max = 255)
        String headline,

        List<@NotBlank @Size(max = 100) String> skills,

        List<@NotBlank @Size(max = 100) String> targetRoles,

        @URL(protocol = "https")
        @Size(max = 500)
        String linkedinUrl,

        @URL(protocol = "https")
        @Size(max = 500)
        String githubUrl,

        @URL(protocol = "https")
        @Size(max = 500)
        String portfolioUrl,

        Boolean hasReferencesAvailable
) {
}

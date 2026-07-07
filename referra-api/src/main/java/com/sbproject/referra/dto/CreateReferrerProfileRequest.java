package com.sbproject.referra.dto;

import com.sbproject.referra.model.ReferralStatus;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record CreateReferrerProfileRequest(

        @NotBlank
        @Size(max = 255)
        String companyName,

        @NotBlank
        @Size(max = 255)
        String jobTitle,

        @Size(max = 100)
        String seniority,

        List<@NotBlank @Size(max = 100) String> skills,

        @Size(max = 1000)
        String bio,

        @NotNull
        ReferralStatus referralStatus,

        @Min(0)
        Integer maxRequestsPerWeek
) {
}

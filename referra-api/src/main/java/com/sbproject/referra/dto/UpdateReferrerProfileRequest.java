package com.sbproject.referra.dto;

import com.sbproject.referra.model.ReferralStatus;
import jakarta.validation.constraints.Size;

import java.util.List;

public record UpdateReferrerProfileRequest(

        @Size(max = 255)
        String companyName,

        @Size(max = 255)
        String jobTitle,

        @Size(max = 100)
        String seniority,

        List<@Size(max = 100) String> skills,

        @Size(max = 1000)
        String bio,

        ReferralStatus referralStatus,

        Integer maxRequestsPerWeek
) {
}

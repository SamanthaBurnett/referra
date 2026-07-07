package com.sbproject.referra.dto;

import com.sbproject.referra.model.Company;

import java.util.UUID;

public record CompanyResponse(
        UUID id,
        String name,
        String domain,
        String websiteUrl
) {
    public static CompanyResponse from(Company company) {
        return new CompanyResponse(
                company.getId(),
                company.getName(),
                company.getDomain(),
                company.getWebsiteUrl()
        );
    }
}

package com.sbproject.referra.service;

import com.sbproject.referra.model.Company;
import com.sbproject.referra.repository.CompanyRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
public class CompanyService {

    private final CompanyRepository companyRepository;

    public CompanyService(CompanyRepository companyRepository) {
        this.companyRepository = companyRepository;
    }

    @Transactional
    public Company findOrCreateCompany(String name) {
        return findOrCreateCompany(name, null, null);
    }

    @Transactional
    public Company findOrCreateCompany(String name, String domain, String websiteUrl) {
        String normalizedName = normalizeName(name);

        return companyRepository.findByNameIgnoreCase(normalizedName)
                .orElseGet(() -> createCompany(normalizedName, domain, websiteUrl));
    }

    private Company createCompany(String name, String domain, String websiteUrl) {
        Instant now = Instant.now();

        Company company = Company.builder()
                .name(name)
                .domain(normalizeNullable(domain))
                .websiteUrl(normalizeNullable(websiteUrl))
                .createdAt(now)
                .updatedAt(now)
                .build();

        return companyRepository.save(company);
    }

    private String normalizeName(String name) {
        return name.trim();
    }

    private String normalizeNullable(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        return value.trim();
    }
}

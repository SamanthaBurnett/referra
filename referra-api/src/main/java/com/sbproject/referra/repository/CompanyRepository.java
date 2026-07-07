package com.sbproject.referra.repository;

import com.sbproject.referra.model.Company;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CompanyRepository extends JpaRepository<Company, UUID> {

    Optional<Company> findByNameIgnoreCase(String name);
}

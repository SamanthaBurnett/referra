package com.sbproject.referra.service;

import com.sbproject.referra.model.Company;
import com.sbproject.referra.repository.CompanyRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CompanyServiceTest {

    public static final String TEST_COMPANY_NAME = "TestCo";
    public static final String TEST_DOMAIN = "testco.com";
    public static final String TEST_COMPANY_URL = "https://testco.com";

    @Mock
    private CompanyRepository companyRepository;

    @InjectMocks
    private CompanyService companyService;

    @Test
    void findOrCreateCompany_nameExists_returnsExistingCompany() {
        Company existingCompany = Company.builder()
                .name(TEST_COMPANY_NAME)
                .domain(TEST_DOMAIN)
                .websiteUrl(TEST_COMPANY_URL)
                .build();

        when(companyRepository.findByNameIgnoreCase(TEST_COMPANY_NAME))
                .thenReturn(Optional.of(existingCompany));

        Company result = companyService.findOrCreateCompany(
                TEST_COMPANY_NAME,
                TEST_DOMAIN,
                TEST_COMPANY_URL
        );

        assertThat(result).isEqualTo(existingCompany);
        verify(companyRepository, never()).save(any());
    }



    @Test

    void findOrCreateCompany_nameDoesNotExist_createsCompany() {
        when(companyRepository.findByNameIgnoreCase(TEST_COMPANY_NAME))
                .thenReturn(Optional.empty());

        when(companyRepository.save(any(Company.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Company result = companyService.findOrCreateCompany(
                TEST_COMPANY_NAME,
                TEST_DOMAIN,
                TEST_COMPANY_URL
        );

        ArgumentCaptor<Company> captor = ArgumentCaptor.forClass(Company.class);
        verify(companyRepository).save(captor.capture());

        Company savedCompany = captor.getValue();

        assertThat(savedCompany.getName()).isEqualTo(TEST_COMPANY_NAME);
        assertThat(savedCompany.getDomain()).isEqualTo(TEST_DOMAIN);
        assertThat(savedCompany.getWebsiteUrl()).isEqualTo(TEST_COMPANY_URL);
        assertThat(savedCompany.getCreatedAt()).isNotNull();
        assertThat(savedCompany.getUpdatedAt()).isNotNull();
        assertThat(result).isEqualTo(savedCompany);
    }

    @Test
    void findOrCreateCompany_trimsCompanyNameBeforeLookupAndSave() {
        when(companyRepository.findByNameIgnoreCase(TEST_COMPANY_NAME))
                .thenReturn(Optional.empty());

        when(companyRepository.save(any(Company.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Company result = companyService.findOrCreateCompany(
                "  TestCo  ",
                "  testco.com  ",
                "  https://testco.com  "
        );

        assertThat(result.getName()).isEqualTo(TEST_COMPANY_NAME);
        assertThat(result.getDomain()).isEqualTo(TEST_DOMAIN);
        assertThat(result.getWebsiteUrl()).isEqualTo(TEST_COMPANY_URL);
    }

    @Test
    void findOrCreateCompany_storesBlankOptionalFieldsAsNull() {
        when(companyRepository.findByNameIgnoreCase(TEST_COMPANY_NAME))
                .thenReturn(Optional.empty());

        when(companyRepository.save(any(Company.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Company result = companyService.findOrCreateCompany(
                TEST_COMPANY_NAME,
                "",
                "   "
        );

        assertThat(result.getDomain()).isNull();
        assertThat(result.getWebsiteUrl()).isNull();
    }
}

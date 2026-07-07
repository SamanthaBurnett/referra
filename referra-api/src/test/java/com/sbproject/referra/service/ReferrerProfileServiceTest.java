package com.sbproject.referra.service;

import com.sbproject.referra.dto.CreateReferrerProfileRequest;
import com.sbproject.referra.dto.UpdateReferrerProfileRequest;
import com.sbproject.referra.model.Company;
import com.sbproject.referra.model.ReferralStatus;
import com.sbproject.referra.model.ReferrerProfile;
import com.sbproject.referra.model.User;
import com.sbproject.referra.model.UserRole;
import com.sbproject.referra.repository.ReferrerProfileRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.jwt.Jwt;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReferrerProfileServiceTest {

    public static final String TEST_COMPANY_NAME = "TestCo";
    public static final String TEST_JOB_TITLE = "Software Engineer";
    public static final String TEST_SENIORITY = "Senior";
    public static final String TEST_BIO = "I can help backend candidates.";

    @Mock
    private CurrentUserService currentUserService;

    @Mock
    private UserRoleService userRoleService;

    @Mock
    private CompanyService companyService;

    @Mock
    private ReferrerProfileRepository referrerProfileRepository;

    @InjectMocks
    private ReferrerProfileService referrerProfileService;

    @Test
    void createProfile_valid_createsProfileForReferrerUser() {
        Jwt jwt = mock(Jwt.class);
        User user = User.builder().build();
        Company company = Company.builder().name(TEST_COMPANY_NAME).build();

        CreateReferrerProfileRequest request = new CreateReferrerProfileRequest(
                TEST_COMPANY_NAME,
                TEST_JOB_TITLE,
                TEST_SENIORITY,
                List.of("Java", "AWS"),
                TEST_BIO,
                ReferralStatus.OPEN,
                5
        );

        when(currentUserService.getOrCreateUser(jwt)).thenReturn(user);
        when(userRoleService.getRoles(user)).thenReturn(Set.of(UserRole.REFERRER));
        when(referrerProfileRepository.existsByUser(user)).thenReturn(false);
        when(companyService.findOrCreateCompany(TEST_COMPANY_NAME)).thenReturn(company);
        when(referrerProfileRepository.save(any(ReferrerProfile.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        referrerProfileService.createProfile(jwt, request);

        ArgumentCaptor<ReferrerProfile> captor = ArgumentCaptor.forClass(ReferrerProfile.class);
        verify(referrerProfileRepository).save(captor.capture());

        ReferrerProfile savedProfile = captor.getValue();

        assertThat(savedProfile.getUser()).isEqualTo(user);
        assertThat(savedProfile.getCompany()).isEqualTo(company);
        assertThat(savedProfile.getJobTitle()).isEqualTo(TEST_JOB_TITLE);
        assertThat(savedProfile.getSeniority()).isEqualTo(TEST_SENIORITY);
        assertThat(savedProfile.getSkills()).containsExactly("Java", "AWS");
        assertThat(savedProfile.getBio()).isEqualTo(TEST_BIO);
        assertThat(savedProfile.getReferralStatus()).isEqualTo(ReferralStatus.OPEN);
        assertThat(savedProfile.getMaxRequestsPerWeek()).isEqualTo(5);
        assertThat(savedProfile.getCreatedAt()).isNotNull();
        assertThat(savedProfile.getUpdatedAt()).isNotNull();
    }

    @Test
    void createProfile_userDoesNotHaveReferrerRole_throwsException() {
        Jwt jwt = mock(Jwt.class);
        User user = User.builder().build();

        CreateReferrerProfileRequest request = new CreateReferrerProfileRequest(
                TEST_COMPANY_NAME,
                TEST_JOB_TITLE,
                TEST_SENIORITY,
                List.of("Java"),
                null,
                ReferralStatus.OPEN,
                5
        );

        when(currentUserService.getOrCreateUser(jwt)).thenReturn(user);
        when(userRoleService.getRoles(user)).thenReturn(Set.of(UserRole.CANDIDATE));

        assertThatThrownBy(() -> referrerProfileService.createProfile(jwt, request))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("User must have REFERRER role.");

        verify(referrerProfileRepository, never()).save(any());
    }

    @Test
    void createProfile_profileAlreadyExists_throwsException() {
        Jwt jwt = mock(Jwt.class);
        User user = User.builder().build();

        CreateReferrerProfileRequest request = new CreateReferrerProfileRequest(
                TEST_COMPANY_NAME,
                TEST_JOB_TITLE,
                TEST_SENIORITY,
                List.of("Java"),
                null,
                ReferralStatus.OPEN,
                5
        );

        when(currentUserService.getOrCreateUser(jwt)).thenReturn(user);
        when(userRoleService.getRoles(user)).thenReturn(Set.of(UserRole.REFERRER));
        when(referrerProfileRepository.existsByUser(user)).thenReturn(true);

        assertThatThrownBy(() -> referrerProfileService.createProfile(jwt, request))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Referrer profile already exists for this user.");

        verify(referrerProfileRepository, never()).save(any());
    }

    @Test
    void createProfile_nullMaxRequests_usesDefaultMaxRequests() {
        Jwt jwt = mock(Jwt.class);
        User user = User.builder().build();
        Company company = Company.builder().name(TEST_COMPANY_NAME).build();

        CreateReferrerProfileRequest request = new CreateReferrerProfileRequest(
                TEST_COMPANY_NAME,
                TEST_JOB_TITLE,
                TEST_SENIORITY,
                List.of("Java"),
                null,
                ReferralStatus.OPEN,
                null
        );

        when(currentUserService.getOrCreateUser(jwt)).thenReturn(user);
        when(userRoleService.getRoles(user)).thenReturn(Set.of(UserRole.REFERRER));
        when(referrerProfileRepository.existsByUser(user)).thenReturn(false);
        when(companyService.findOrCreateCompany(TEST_COMPANY_NAME)).thenReturn(company);
        when(referrerProfileRepository.save(any(ReferrerProfile.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        var response = referrerProfileService.createProfile(jwt, request);

        assertThat(response.maxRequestsPerWeek()).isEqualTo(5);
    }

    @Test
    void createProfile_maxRequestsIsNegative_throwsException() {
        Jwt jwt = mock(Jwt.class);
        User user = User.builder().build();

        CreateReferrerProfileRequest request = new CreateReferrerProfileRequest(
                TEST_COMPANY_NAME,
                TEST_JOB_TITLE,
                TEST_SENIORITY,
                List.of("Java"),
                null,
                ReferralStatus.OPEN,
                -1
        );

        when(currentUserService.getOrCreateUser(jwt)).thenReturn(user);
        when(userRoleService.getRoles(user)).thenReturn(Set.of(UserRole.REFERRER));
        when(referrerProfileRepository.existsByUser(user)).thenReturn(false);

        assertThatThrownBy(() -> referrerProfileService.createProfile(jwt, request))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Max requests per week cannot be negative.");

        verify(referrerProfileRepository, never()).save(any());
    }

    @Test
    void getProfile_profileExists_returnsExistingProfile() {
        Jwt jwt = mock(Jwt.class);
        User user = User.builder().build();
        Company company = Company.builder().name(TEST_COMPANY_NAME).build();

        ReferrerProfile profile = ReferrerProfile.builder()
                .user(user)
                .company(company)
                .jobTitle(TEST_JOB_TITLE)
                .seniority(TEST_SENIORITY)
                .skills(new String[]{"Java"})
                .bio(TEST_BIO)
                .referralStatus(ReferralStatus.OPEN)
                .maxRequestsPerWeek(5)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        when(currentUserService.getOrCreateUser(jwt)).thenReturn(user);
        when(referrerProfileRepository.findByUser(user)).thenReturn(Optional.of(profile));

        var response = referrerProfileService.getProfile(jwt);

        assertThat(response.company().name()).isEqualTo(TEST_COMPANY_NAME);
        assertThat(response.jobTitle()).isEqualTo(TEST_JOB_TITLE);
        assertThat(response.seniority()).isEqualTo(TEST_SENIORITY);
        assertThat(response.skills()).containsExactly("Java");
        assertThat(response.referralStatus()).isEqualTo(ReferralStatus.OPEN);
    }

    @Test
    void updateProfile_updatesOnlyProvidedFields() {
        Jwt jwt = mock(Jwt.class);
        User user = User.builder().build();
        Company originalCompany = Company.builder().name(TEST_COMPANY_NAME).build();

        ReferrerProfile profile = ReferrerProfile.builder()
                .user(user)
                .company(originalCompany)
                .jobTitle(TEST_JOB_TITLE)
                .seniority("Mid")
                .skills(new String[]{"Java"})
                .bio("Old bio")
                .referralStatus(ReferralStatus.OPEN)
                .maxRequestsPerWeek(5)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        UpdateReferrerProfileRequest request = new UpdateReferrerProfileRequest(
                null,
                null,
                TEST_SENIORITY,
                List.of("Java", "AWS"),
                "Updated bio",
                ReferralStatus.LIMITED,
                3
        );

        when(currentUserService.getOrCreateUser(jwt)).thenReturn(user);
        when(userRoleService.getRoles(user)).thenReturn(Set.of(UserRole.REFERRER));
        when(referrerProfileRepository.findByUser(user)).thenReturn(Optional.of(profile));

        var response = referrerProfileService.updateProfile(jwt, request);

        assertThat(response.company().name()).isEqualTo(TEST_COMPANY_NAME);
        assertThat(response.jobTitle()).isEqualTo(TEST_JOB_TITLE);
        assertThat(response.seniority()).isEqualTo(TEST_SENIORITY);
        assertThat(response.skills()).containsExactly("Java", "AWS");
        assertThat(response.bio()).isEqualTo("Updated bio");
        assertThat(response.referralStatus()).isEqualTo(ReferralStatus.LIMITED);
        assertThat(response.maxRequestsPerWeek()).isEqualTo(3);
    }

    @Test
    void updateProfile_companyNameProvided_changesCompanyName() {
        Jwt jwt = mock(Jwt.class);
        User user = User.builder().build();
        Company originalCompany = Company.builder().name(TEST_COMPANY_NAME).build();
        Company newCompany = Company.builder().name("Test LLC").build();

        ReferrerProfile profile = ReferrerProfile.builder()
                .user(user)
                .company(originalCompany)
                .jobTitle(TEST_JOB_TITLE)
                .seniority(TEST_SENIORITY)
                .skills(new String[]{"Java"})
                .bio(null)
                .referralStatus(ReferralStatus.OPEN)
                .maxRequestsPerWeek(5)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        UpdateReferrerProfileRequest request = new UpdateReferrerProfileRequest(
                "Test LLC",
                null,
                null,
                null,
                null,
                null,
                null
        );

        when(currentUserService.getOrCreateUser(jwt)).thenReturn(user);
        when(userRoleService.getRoles(user)).thenReturn(Set.of(UserRole.REFERRER));
        when(referrerProfileRepository.findByUser(user)).thenReturn(Optional.of(profile));
        when(companyService.findOrCreateCompany("Test LLC")).thenReturn(newCompany);

        var response = referrerProfileService.updateProfile(jwt, request);

        assertThat(response.company().name()).isEqualTo("Test LLC");
        assertThat(profile.getCompany()).isEqualTo(newCompany);
    }
}

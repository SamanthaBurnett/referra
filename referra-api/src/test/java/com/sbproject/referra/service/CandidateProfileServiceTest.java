package com.sbproject.referra.service;

import com.sbproject.referra.dto.CreateCandidateProfileRequest;
import com.sbproject.referra.dto.UpdateCandidateProfileRequest;
import com.sbproject.referra.model.CandidateProfile;
import com.sbproject.referra.model.User;
import com.sbproject.referra.model.UserRole;
import com.sbproject.referra.repository.CandidateProfileRepository;
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
class CandidateProfileServiceTest {

    public static final String TEST_FIRST_NAME = "Jane";
    public static final String TEST_LAST_NAME = "Doe";
    public static final String TEST_HEADLINE = "Backend engineer";
    public static final String TEST_LINKEDIN_URL = "https://linkedin.com/in/example";
    public static final String TEST_GITHUB_URL = "https://github.com/example";
    public static final String TEST_PORTFOLIO_URL = "https://example.com";

    @Mock
    private CurrentUserService currentUserService;

    @Mock
    private UserRoleService userRoleService;

    @Mock
    private CandidateProfileRepository candidateProfileRepository;

    @InjectMocks
    private CandidateProfileService candidateProfileService;

    @Test
    void createProfile_valid_createsProfileForCandidateUser() {
        Jwt jwt = mock(Jwt.class);
        User user = User.builder().build();

        CreateCandidateProfileRequest request = new CreateCandidateProfileRequest(
                TEST_FIRST_NAME,
                TEST_LAST_NAME,
                TEST_HEADLINE,
                List.of("Java", "Spring Boot"),
                List.of("Backend Engineer"),
                TEST_LINKEDIN_URL,
                TEST_GITHUB_URL,
                TEST_PORTFOLIO_URL,
                true
        );

        when(currentUserService.getOrCreateUser(jwt)).thenReturn(user);

        when(userRoleService.getRoles(user)).thenReturn(Set.of(UserRole.CANDIDATE));

        when(candidateProfileRepository.existsByUser(user)).thenReturn(false);

        when(candidateProfileRepository.save(any(CandidateProfile.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        candidateProfileService.createProfile(jwt, request);

        ArgumentCaptor<CandidateProfile> captor = ArgumentCaptor.forClass(CandidateProfile.class);
        verify(candidateProfileRepository).save(captor.capture());

        CandidateProfile savedProfile = captor.getValue();

        assertThat(savedProfile.getUser()).isEqualTo(user);
        assertThat(savedProfile.getFirstName()).isEqualTo(TEST_FIRST_NAME);
        assertThat(savedProfile.getLastName()).isEqualTo(TEST_LAST_NAME);
        assertThat(savedProfile.getHeadline()).isEqualTo(TEST_HEADLINE);
        assertThat(savedProfile.getSkills()).containsExactly("Java", "Spring Boot");
        assertThat(savedProfile.getTargetRoles()).containsExactly("Backend Engineer");
        assertThat(savedProfile.getLinkedinUrl()).isEqualTo(TEST_LINKEDIN_URL);
        assertThat(savedProfile.getGithubUrl()).isEqualTo(TEST_GITHUB_URL);
        assertThat(savedProfile.getPortfolioUrl()).isEqualTo(TEST_PORTFOLIO_URL);
        assertThat(savedProfile.isHasReferencesAvailable()).isTrue();
        assertThat(savedProfile.getCreatedAt()).isNotNull();
        assertThat(savedProfile.getUpdatedAt()).isNotNull();
    }

    @Test
    void createProfile_userDoesNotHaveCandidateRole_ThrowsException() {
        Jwt jwt = mock(Jwt.class);
        User user = User.builder().build();

        CreateCandidateProfileRequest request = new CreateCandidateProfileRequest(
                TEST_FIRST_NAME,
                TEST_LAST_NAME,
                TEST_HEADLINE,
                List.of("Java"),
                List.of("Backend Engineer"),
                null,
                null,
                null,
                false
        );

        when(currentUserService.getOrCreateUser(jwt)).thenReturn(user);

        when(userRoleService.getRoles(user)).thenReturn(Set.of(UserRole.REFERRER));

        assertThatThrownBy(() -> candidateProfileService.createProfile(jwt, request))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("User must have CANDIDATE role.");

        verify(candidateProfileRepository, never()).save(any());
    }

    @Test
    void createProfile_profileAlreadyExists_throwsException() {
        Jwt jwt = mock(Jwt.class);
        User user = User.builder().build();

        CreateCandidateProfileRequest request = new CreateCandidateProfileRequest(
                TEST_FIRST_NAME,
                TEST_LAST_NAME,
                TEST_HEADLINE,
                List.of("Java"),
                List.of("Backend Engineer"),
                null,
                null,
                null,
                false
        );

        when(currentUserService.getOrCreateUser(jwt)).thenReturn(user);

        when(userRoleService.getRoles(user)).thenReturn(Set.of(UserRole.CANDIDATE));

        when(candidateProfileRepository.existsByUser(user)).thenReturn(true);

        assertThatThrownBy(() -> candidateProfileService.createProfile(jwt, request))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Candidate profile already exists for this user.");

        verify(candidateProfileRepository, never()).save(any());
    }

    @Test
    void getProfile_profileExists_returnsProfile() {
        Jwt jwt = mock(Jwt.class);
        User user = User.builder().build();

        CandidateProfile profile = CandidateProfile.builder()
                .id(null)
                .user(user)
                .firstName(TEST_FIRST_NAME)
                .lastName(TEST_LAST_NAME)
                .headline(TEST_HEADLINE)
                .skills(new String[]{"Java"})
                .targetRoles(new String[]{"Backend Engineer"})
                .hasReferencesAvailable(false)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        when(currentUserService.getOrCreateUser(jwt)).thenReturn(user);

        when(candidateProfileRepository.findByUser(user)).thenReturn(Optional.of(profile));

        var response = candidateProfileService.getProfile(jwt);

        assertThat(response.firstName()).isEqualTo(TEST_FIRST_NAME);
        assertThat(response.lastName()).isEqualTo(TEST_LAST_NAME);
        assertThat(response.headline()).isEqualTo(TEST_HEADLINE);
        assertThat(response.skills()).containsExactly("Java");
        assertThat(response.targetRoles()).containsExactly("Backend Engineer");
    }

    @Test
    void updateProfile_updatesOnlyProvidedFields() {
        Jwt jwt = mock(Jwt.class);
        User user = User.builder().build();

        CandidateProfile profile = CandidateProfile.builder()
                .user(user)
                .firstName(TEST_FIRST_NAME)
                .lastName(TEST_LAST_NAME)
                .headline("Old headline")
                .skills(new String[]{"Java"})
                .targetRoles(new String[]{"Backend Engineer"})
                .linkedinUrl(null)
                .githubUrl(null)
                .portfolioUrl(null)
                .hasReferencesAvailable(false)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        UpdateCandidateProfileRequest request = new UpdateCandidateProfileRequest(
                null,
                null,
                "Updated headline",
                List.of("Java", "AWS"),
                null,
                TEST_LINKEDIN_URL,
                null,
                null,
                true
        );

        when(currentUserService.getOrCreateUser(jwt)).thenReturn(user);

        when(userRoleService.getRoles(user)).thenReturn(Set.of(UserRole.CANDIDATE));

        when(candidateProfileRepository.findByUser(user)).thenReturn(Optional.of(profile));

        var response = candidateProfileService.updateProfile(jwt, request);

        assertThat(response.firstName()).isEqualTo(TEST_FIRST_NAME);
        assertThat(response.lastName()).isEqualTo(TEST_LAST_NAME);
        assertThat(response.headline()).isEqualTo("Updated headline");
        assertThat(response.skills()).containsExactly("Java", "AWS");
        assertThat(response.targetRoles()).containsExactly("Backend Engineer");
        assertThat(response.linkedinUrl()).isEqualTo(TEST_LINKEDIN_URL);
        assertThat(response.hasReferencesAvailable()).isTrue();
    }
}

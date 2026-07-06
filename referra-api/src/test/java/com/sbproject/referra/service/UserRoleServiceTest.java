package com.sbproject.referra.service;

import com.sbproject.referra.model.User;
import com.sbproject.referra.model.UserRole;
import com.sbproject.referra.model.UserRoleEntity;
import com.sbproject.referra.repository.UserRoleRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserRoleServiceTest {

    @Mock
    private UserRoleRepository userRoleRepository;

    @InjectMocks
    private UserRoleService userRoleService;

    @Test
    void getRoles_returnsUserRoles() {
        User user = new User();

        UserRoleEntity roleEntity = UserRoleEntity.builder()
                .user(user)
                .role(UserRole.CANDIDATE)
                .build();

        when(userRoleRepository.findByUser(user))
                .thenReturn(List.of(roleEntity));

        Set<UserRole> roles = userRoleService.getRoles(user);

        assertThat(roles).containsExactly(UserRole.CANDIDATE);
    }

    @Test
    void addRole_whenUserHasNoExistingRole_savesRole() {
        User user = new User();

        when(userRoleRepository.findByUser(user))
                .thenReturn(List.of())
                .thenReturn(List.of(
                        UserRoleEntity.builder()
                                .user(user)
                                .role(UserRole.CANDIDATE)
                                .build()
                ));

        Set<UserRole> roles = userRoleService.addRole(user, UserRole.CANDIDATE);

        ArgumentCaptor<UserRoleEntity> captor = ArgumentCaptor.forClass(UserRoleEntity.class);
        verify(userRoleRepository).save(captor.capture());

        UserRoleEntity savedRole = captor.getValue();

        assertThat(savedRole.getUser()).isEqualTo(user);
        assertThat(savedRole.getRole()).isEqualTo(UserRole.CANDIDATE);
        assertThat(savedRole.getCreatedAt()).isNotNull();
        assertThat(roles).containsExactly(UserRole.CANDIDATE);
    }

    @Test
    void addRole_whenUserAlreadyHasRole_throwsException() {
        User user = new User();

        when(userRoleRepository.findByUser(user))
                .thenReturn(List.of(
                        UserRoleEntity.builder()
                                .user(user)
                                .role(UserRole.CANDIDATE)
                                .build()
                ));

        assertThatThrownBy(() -> userRoleService.addRole(user, UserRole.REFERRER))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("A role has already been assigned to this user.");

        verify(userRoleRepository, never()).save(any());
    }
}
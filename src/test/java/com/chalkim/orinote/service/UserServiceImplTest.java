package com.chalkim.orinote.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.chalkim.orinote.dao.UserDao;
import com.chalkim.orinote.dao.UserOAuthAccountDao;
import com.chalkim.orinote.dto.UserDto;
import com.chalkim.orinote.exception.NotFoundException;
import com.chalkim.orinote.model.User;
import com.chalkim.orinote.model.UserOAuthAccount;
import com.chalkim.orinote.service.impl.UserServiceImpl;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserDao userDao;

    @Mock
    private UserOAuthAccountDao userOAuthAccountDao;

    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        userService = new UserServiceImpl(userDao, userOAuthAccountDao);
    }

    @Test
    void createUser_Success_ReturnsUser() {
        // Arrange
        UserDto userDto = new UserDto();
        userDto.setUsername("testuser");
        userDto.setEmail("test@example.com");
        userDto.setRole("ROLE_USER");
        userDto.setEnabled(true);

        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setUsername("testuser");
        savedUser.setEmail("test@example.com");
        savedUser.setRole("ROLE_USER");
        savedUser.setEnabled(true);

        when(userDao.save(any(User.class))).thenReturn(savedUser);

        // Act
        User result = userService.createUser(userDto);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("testuser", result.getUsername());
        verify(userDao).save(any(User.class));
    }

    @Test
    void bindGitHubAccount_Success_BindsAccount() {
        // Arrange
        Long userId = 1L;
        String providerUserId = "12345";

        when(userDao.existsById(userId)).thenReturn(true);
        when(userOAuthAccountDao.findByProviderAndProviderUserId("github", providerUserId))
                .thenReturn(Optional.empty());

        // Act
        userService.bindGitHubAccount(userId, providerUserId);

        // Assert
        verify(userOAuthAccountDao).save(any(UserOAuthAccount.class));
    }

    @Test
    void bindGitHubAccount_UserNotFound_ThrowsException() {
        // Arrange
        Long userId = 999L;
        String providerUserId = "12345";

        when(userDao.existsById(userId)).thenReturn(false);

        // Act & Assert
        assertThrows(NotFoundException.class, 
                () -> userService.bindGitHubAccount(userId, providerUserId));
    }

    @Test
    void bindGitHubAccount_AlreadyBoundToDifferentUser_ThrowsException() {
        // Arrange
        Long userId = 1L;
        String providerUserId = "12345";

        UserOAuthAccount existingBinding = new UserOAuthAccount();
        existingBinding.setUserId(2L); // Different user
        existingBinding.setProvider("github");
        existingBinding.setProviderUserId(providerUserId);

        when(userDao.existsById(userId)).thenReturn(true);
        when(userOAuthAccountDao.findByProviderAndProviderUserId("github", providerUserId))
                .thenReturn(Optional.of(existingBinding));

        // Act & Assert
        assertThrows(IllegalArgumentException.class, 
                () -> userService.bindGitHubAccount(userId, providerUserId));
    }

    @Test
    void setUserEnabled_Success_UpdatesStatus() {
        // Arrange
        Long userId = 1L;
        boolean enabled = false;

        when(userDao.existsById(userId)).thenReturn(true);
        when(userDao.setEnabled(userId, enabled)).thenReturn(1);

        // Act
        userService.setUserEnabled(userId, enabled);

        // Assert
        verify(userDao).setEnabled(userId, enabled);
    }

    @Test
    void setUserEnabled_UserNotFound_ThrowsException() {
        // Arrange
        Long userId = 999L;
        boolean enabled = false;

        when(userDao.existsById(userId)).thenReturn(false);

        // Act & Assert
        assertThrows(NotFoundException.class, 
                () -> userService.setUserEnabled(userId, enabled));
    }
}
package com.chalkim.orinote.security;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.chalkim.orinote.model.User;

class CustomOAuth2UserTest {

    @Test
    void constructor_Success_CreatesCustomOAuth2User() {
        // Arrange
        User localUser = new User();
        localUser.setId(1L);
        localUser.setUsername("testuser");
        localUser.setEmail("test@example.com");
        localUser.setRole("ROLE_USER");
        localUser.setEnabled(true);

        Map<String, Object> attributes = new HashMap<>();
        attributes.put("id", 12345);
        attributes.put("login", "testuser");

        // Act
        CustomOAuth2User customOAuth2User = new CustomOAuth2User(localUser, attributes);

        // Assert
        assertEquals(1L, customOAuth2User.getUserId());
        assertEquals("testuser", customOAuth2User.getName());
        assertEquals("test@example.com", customOAuth2User.getEmail());
        assertEquals("ROLE_USER", customOAuth2User.getRole());
        assertTrue(customOAuth2User.isEnabled());
        assertEquals(attributes, customOAuth2User.getAttributes());
        assertEquals(1, customOAuth2User.getAuthorities().size());
        assertTrue(customOAuth2User.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_USER")));
    }

    @Test
    void isEnabled_WithNullEnabled_ReturnsFalse() {
        // Arrange
        User localUser = new User();
        localUser.setId(1L);
        localUser.setUsername("testuser");
        localUser.setEnabled(null); // null enabled

        Map<String, Object> attributes = new HashMap<>();

        // Act
        CustomOAuth2User customOAuth2User = new CustomOAuth2User(localUser, attributes);

        // Assert
        assertFalse(customOAuth2User.isEnabled());
    }

    @Test
    void isEnabled_WithFalseEnabled_ReturnsFalse() {
        // Arrange
        User localUser = new User();
        localUser.setId(1L);
        localUser.setUsername("testuser");
        localUser.setEnabled(false);

        Map<String, Object> attributes = new HashMap<>();

        // Act
        CustomOAuth2User customOAuth2User = new CustomOAuth2User(localUser, attributes);

        // Assert
        assertFalse(customOAuth2User.isEnabled());
    }
}
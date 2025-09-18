package com.chalkim.orinote.security;

import java.util.Optional;

import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import com.chalkim.orinote.dao.UserDao;
import com.chalkim.orinote.dao.UserOAuthAccountDao;
import com.chalkim.orinote.model.User;
import com.chalkim.orinote.model.UserOAuthAccount;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class OAuth2UserServiceImpl extends DefaultOAuth2UserService {
    
    private final UserOAuthAccountDao userOAuthAccountDao;
    private final UserDao userDao;

    public OAuth2UserServiceImpl(UserOAuthAccountDao userOAuthAccountDao, UserDao userDao) {
        this.userOAuthAccountDao = userOAuthAccountDao;
        this.userDao = userDao;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        // Load OAuth2 user from provider
        OAuth2User oAuth2User = super.loadUser(userRequest);
        
        try {
            return processOAuth2User(userRequest, oAuth2User);
        } catch (Exception ex) {
            log.error("Error processing OAuth2 user: {}", ex.getMessage(), ex);
            throw new OAuth2AuthenticationException(
                new OAuth2Error("authentication_error", "Authentication failed", null), 
                ex.getMessage(), ex
            );
        }
    }

    private OAuth2User processOAuth2User(OAuth2UserRequest userRequest, OAuth2User oAuth2User) {
        String provider = userRequest.getClientRegistration().getRegistrationId();
        
        // Extract provider user ID (handle different possible types)
        String providerUserId = extractProviderUserId(oAuth2User);
        
        log.info("Processing OAuth2 user: provider={}, providerUserId={}", provider, providerUserId);
        
        // Find OAuth account binding
        Optional<UserOAuthAccount> oauthAccountOpt = userOAuthAccountDao
                .findByProviderAndProviderUserId(provider, providerUserId);
        
        if (oauthAccountOpt.isEmpty()) {
            log.warn("No OAuth account binding found for provider={}, providerUserId={}", provider, providerUserId);
            throw new OAuth2AuthenticationException(
                new OAuth2Error("unauthorized_github_user", 
                    "Your GitHub account is not authorized. Please contact administrator.", null)
            );
        }
        
        UserOAuthAccount oauthAccount = oauthAccountOpt.get();
        
        // Load local user
        Optional<User> userOpt = userDao.findById(oauthAccount.getUserId());
        if (userOpt.isEmpty()) {
            log.error("OAuth account references non-existent user: userId={}", oauthAccount.getUserId());
            throw new OAuth2AuthenticationException(
                new OAuth2Error("user_not_found", 
                    "Associated user account not found. Please contact administrator.", null)
            );
        }
        
        User localUser = userOpt.get();
        
        // Check if user is enabled
        if (!Boolean.TRUE.equals(localUser.getEnabled())) {
            log.warn("User account is disabled: userId={}, username={}", localUser.getId(), localUser.getUsername());
            throw new OAuth2AuthenticationException(
                new OAuth2Error("account_disabled", 
                    "Your account has been disabled. Please contact administrator.", null)
            );
        }
        
        log.info("OAuth2 authentication successful for user: id={}, username={}, role={}", 
                localUser.getId(), localUser.getUsername(), localUser.getRole());
        
        return new CustomOAuth2User(localUser, oAuth2User.getAttributes());
    }

    private String extractProviderUserId(OAuth2User oAuth2User) {
        Object idAttr = oAuth2User.getAttribute("id");
        if (idAttr == null) {
            throw new OAuth2AuthenticationException(
                new OAuth2Error("invalid_user_info", "Provider user ID not found", null)
            );
        }
        
        // Handle different possible types for ID (Integer, Long, String)
        return idAttr.toString();
    }
}
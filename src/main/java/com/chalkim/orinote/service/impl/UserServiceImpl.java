package com.chalkim.orinote.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.chalkim.orinote.dao.UserDao;
import com.chalkim.orinote.dao.UserOAuthAccountDao;
import com.chalkim.orinote.dto.UserDto;
import com.chalkim.orinote.exception.NotFoundException;
import com.chalkim.orinote.model.User;
import com.chalkim.orinote.model.UserOAuthAccount;
import com.chalkim.orinote.service.UserService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UserDao userDao;
    private final UserOAuthAccountDao userOAuthAccountDao;

    public UserServiceImpl(UserDao userDao, UserOAuthAccountDao userOAuthAccountDao) {
        this.userDao = userDao;
        this.userOAuthAccountDao = userOAuthAccountDao;
    }

    @Override
    public User createUser(UserDto userDto) {
        User user = new User();
        user.setUsername(userDto.getUsername());
        user.setEmail(userDto.getEmail());
        user.setRole(userDto.getRole());
        user.setEnabled(userDto.getEnabled());
        
        User savedUser = userDao.save(user);
        log.info("Created user: id={}, username={}", savedUser.getId(), savedUser.getUsername());
        return savedUser;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> getUserById(Long id) {
        return userDao.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> getUserByUsername(String username) {
        return userDao.findByUsername(username);
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        return userDao.findAll();
    }

    @Override
    public User updateUser(Long id, UserDto userDto) {
        User existingUser = userDao.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + id));
        
        existingUser.setUsername(userDto.getUsername());
        existingUser.setEmail(userDto.getEmail());
        existingUser.setRole(userDto.getRole());
        existingUser.setEnabled(userDto.getEnabled());
        
        User updatedUser = userDao.save(existingUser);
        log.info("Updated user: id={}, username={}", updatedUser.getId(), updatedUser.getUsername());
        return updatedUser;
    }

    @Override
    public void setUserEnabled(Long id, boolean enabled) {
        if (!userDao.existsById(id)) {
            throw new NotFoundException("User not found with id: " + id);
        }
        
        userDao.setEnabled(id, enabled);
        log.info("Set user enabled status: id={}, enabled={}", id, enabled);
    }

    @Override
    public void bindGitHubAccount(Long userId, String providerUserId) {
        if (!userDao.existsById(userId)) {
            throw new NotFoundException("User not found with id: " + userId);
        }
        
        // Check if GitHub account is already bound to another user
        Optional<UserOAuthAccount> existingBinding = userOAuthAccountDao
                .findByProviderAndProviderUserId("github", providerUserId);
        
        if (existingBinding.isPresent()) {
            if (!existingBinding.get().getUserId().equals(userId)) {
                throw new IllegalArgumentException(
                    "GitHub account " + providerUserId + " is already bound to another user"
                );
            }
            // Already bound to the same user, nothing to do
            return;
        }
        
        UserOAuthAccount oauthAccount = new UserOAuthAccount();
        oauthAccount.setUserId(userId);
        oauthAccount.setProvider("github");
        oauthAccount.setProviderUserId(providerUserId);
        
        userOAuthAccountDao.save(oauthAccount);
        log.info("Bound GitHub account: userId={}, providerUserId={}", userId, providerUserId);
    }

    @Override
    public void unbindGitHubAccount(Long userId) {
        List<UserOAuthAccount> accounts = userOAuthAccountDao.findByUserId(userId);
        
        accounts.stream()
                .filter(account -> "github".equals(account.getProvider()))
                .forEach(account -> {
                    userOAuthAccountDao.deleteById(account.getId());
                    log.info("Unbound GitHub account: userId={}, providerUserId={}", 
                            userId, account.getProviderUserId());
                });
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsById(Long id) {
        return userDao.existsById(id);
    }
}
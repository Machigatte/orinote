package com.chalkim.orinote.service;

import java.util.List;
import java.util.Optional;

import com.chalkim.orinote.dto.UserDto;
import com.chalkim.orinote.model.User;

public interface UserService {
    
    /**
     * 创建新用户
     * @param userDto 用户数据
     * @return 创建的用户
     */
    User createUser(UserDto userDto);
    
    /**
     * 根据ID获取用户
     * @param id 用户ID
     * @return 用户信息
     */
    Optional<User> getUserById(Long id);
    
    /**
     * 根据用户名获取用户
     * @param username 用户名
     * @return 用户信息
     */
    Optional<User> getUserByUsername(String username);
    
    /**
     * 获取所有用户
     * @return 用户列表
     */
    List<User> getAllUsers();
    
    /**
     * 更新用户信息
     * @param id 用户ID
     * @param userDto 更新的用户数据
     * @return 更新后的用户
     */
    User updateUser(Long id, UserDto userDto);
    
    /**
     * 启用/禁用用户
     * @param id 用户ID
     * @param enabled 是否启用
     */
    void setUserEnabled(Long id, boolean enabled);
    
    /**
     * 绑定用户与GitHub账号
     * @param userId 用户ID
     * @param providerUserId GitHub用户ID
     */
    void bindGitHubAccount(Long userId, String providerUserId);
    
    /**
     * 解绑用户与GitHub账号
     * @param userId 用户ID
     */
    void unbindGitHubAccount(Long userId);
    
    /**
     * 检查用户是否存在
     * @param id 用户ID
     * @return 是否存在
     */
    boolean existsById(Long id);
}
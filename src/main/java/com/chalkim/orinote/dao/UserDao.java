package com.chalkim.orinote.dao;

import com.chalkim.orinote.model.User;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserDao {
    
    /**
     * 根据用户名查询用户
     * 
     * @param username 用户名
     * @return 用户对象，如果不存在则返回空
     */
    Optional<User> findByUsername(String username);
    
    /**
     * 根据ID查询用户
     * 
     * @param id 用户ID
     * @return 用户对象，如果不存在则返回空
     */
    Optional<User> findById(Long id);
    
    /**
     * 保存用户
     * 
     * @param user 用户对象
     * @return 保存后的用户对象（包含ID）
     */
    User save(User user);
}
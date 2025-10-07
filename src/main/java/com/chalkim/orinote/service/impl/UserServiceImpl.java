package com.chalkim.orinote.service.impl;

import com.chalkim.orinote.dao.UserDao;
import com.chalkim.orinote.model.User;
import com.chalkim.orinote.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final UserDao userDao;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UserDao userDao, PasswordEncoder passwordEncoder) {
        this.userDao = userDao;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return userDao.findByUsername(username);
    }

    @Override
    public Optional<User> findById(Long id) {
        return userDao.findById(id);
    }

    @Override
    public User save(User user) {
        // 如果是新用户或密码已更改，则对密码进行加密
        if (user.getId() == null || user.getPasswd() != null) {
            user.setPasswd(passwordEncoder.encode(user.getPasswd()));
        }
        return userDao.save(user);
    }
}
package com.chalkim.orinote.service.impl;

import org.springframework.stereotype.Service;

import com.chalkim.orinote.model.User;
import com.chalkim.orinote.service.UserService;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class UserServiceImpl implements UserService{
    
    public User getCurrentUser(User user) {
        return user;
    }
}

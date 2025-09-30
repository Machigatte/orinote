package com.chalkim.orinote.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class UserController {

    @GetMapping("/user")
    public Map<String, Object> getCurrentUser(Principal principal) {
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("username", principal.getName());
        return userInfo;
    }
}
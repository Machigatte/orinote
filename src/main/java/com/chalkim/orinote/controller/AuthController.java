package com.chalkim.orinote.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AuthController {
    @GetMapping("/login")
    public String login() {
        // 直接返回静态HTML文件名，Spring Boot会自动从static目录查找
        return "redirect:/login.html";
    }
}
package com.chalkim.orinote.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.view.RedirectView;

@Controller
public class AuthController {

    @GetMapping("/login")
    public String login() {
        // 直接返回静态HTML文件名，Spring Boot会自动从static目录查找
        return "redirect:/login.html";
    }

    @GetMapping("/home")
    public RedirectView home() {
        // 重定向到静态HTML页面
        return new RedirectView("/home.html");
    }
    
    @GetMapping("/")
    public RedirectView root() {
        // 根路径重定向到home
        return new RedirectView("/home");
    }
}
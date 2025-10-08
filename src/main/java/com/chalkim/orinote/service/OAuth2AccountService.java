package com.chalkim.orinote.service;

import org.springframework.stereotype.Service;

import com.chalkim.orinote.dao.OAuth2UserAccountDao;
import com.chalkim.orinote.dao.UserDao;
import com.chalkim.orinote.model.OAuth2UserAccount;
import com.chalkim.orinote.model.User;
import com.chalkim.orinote.security.JwtUtil;

@Service
public class OAuth2AccountService {
    private final OAuth2UserAccountDao oauth2UserAccountDao;
    private final UserDao userDao; // 假设有一个 UserDao 用于操作用户数据
    private final JwtUtil jwtUtil;


    /**
     * 校验外部账户是否存在，返回 userId，若不存在返回 null
     */
    public Long valid(String provider, String idFromProvider) {
        OAuth2UserAccount account = oauth2UserAccountDao.findByProviderAndId(provider, idFromProvider);
        return account != null ? account.getUserId() : null;
    }

    /**
     * 完整的外部账户登录逻辑：
     * 1. 查询外部账户是否存在
     * 2. 存在则返回 userId 并生成 jwt
     * 3. 不存在则创建随机用户、外部账户记录，返回新 userId 并生成 jwt
     */

    public OAuth2AccountService(OAuth2UserAccountDao oauth2UserAccountDao, UserDao userDao, JwtUtil jwtUtil) {
        this.oauth2UserAccountDao = oauth2UserAccountDao;
        this.userDao = userDao;
        this.jwtUtil = jwtUtil;
    }

    public String loginOrRegister(String provider, String idFromProvider, String usernameHint) {
        OAuth2UserAccount account = oauth2UserAccountDao.findByProviderAndId(provider, idFromProvider);
        Long userId;
        // TODO: 如果当前已经了登录账号，绑定而不是创建新用户
         // if (currentUserId != null) { ... }
        if (account != null) {
            userId = account.getUserId();
        } else {
            // 创建随机用户名
            String username = usernameHint != null ? usernameHint : provider + "_" + idFromProvider;
            User user = new User();
            user.setUsername(username);
            user.setPasswd(null); // 社交用户无密码
            userId = userDao.save(user).getId();
            // 创建外部账户记录
            OAuth2UserAccount newAccount = new OAuth2UserAccount(provider, idFromProvider, userId);
            oauth2UserAccountDao.save(newAccount);
        }
        // 构造一个 Authentication 用于生成 jwt
        return jwtUtil.generateToken(userId.toString());
    }
}

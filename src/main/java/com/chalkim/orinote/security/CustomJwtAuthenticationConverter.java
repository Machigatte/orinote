package com.chalkim.orinote.security;

import java.util.Collection;
import java.util.List;

import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import com.chalkim.orinote.model.User;
import com.chalkim.orinote.repository.UserRepository;

public class CustomJwtAuthenticationConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    private final UserRepository userRepository;

    public CustomJwtAuthenticationConverter(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public AbstractAuthenticationToken convert(@NonNull Jwt jwt) {
        String sub = jwt.getClaim("sub");
        User user = userRepository.findBySub(sub)
                .orElseGet(() -> {
                    // If user does not exist, create a new one
                    User newUser = new User();
                    newUser.setSub(sub);
                    return userRepository.save(newUser);
                });

        Collection<? extends GrantedAuthority> authorities = List.of();
        
        return new UsernamePasswordAuthenticationToken(user, jwt, authorities);
    }
}

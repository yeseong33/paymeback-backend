package com.paymeback.domain.security;

import com.paymeback.domain.user.service.UserService;
import com.paymeback.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserService userService;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        try {
            User user = userService.findByEmail(email);

            return org.springframework.security.core.userdetails.User.builder()
                .username(user.email())
                .password(user.password())
                .authorities(Collections.singletonList(new SimpleGrantedAuthority(user.role().getKey())))
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .disabled(!user.verified())
                .build();

        } catch (Exception e) {
            throw new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + email, e);
        }
    }
}
package com.roomiematcher.service;

import com.roomiematcher.model.User;
import com.roomiematcher.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email);
        
        if (user == null) {
            throw new UsernameNotFoundException("User not found with email: " + email);
        }
        
        // Check if the user is active (email verified)
        boolean enabled = user.getIsActive() != null && user.getIsActive();
        
        return new org.springframework.security.core.userdetails.User(
            user.getEmail(),
            user.getPassword(),
            enabled, // account enabled
            true,    // account non-expired
            true,    // credentials non-expired
            true,    // account non-locked
            Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
        );
    }
} 
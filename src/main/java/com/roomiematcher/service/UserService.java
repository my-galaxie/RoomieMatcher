package com.roomiematcher.service;

import com.roomiematcher.model.User;
import com.roomiematcher.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    // For simplicity, we're directly instantiating a BCryptPasswordEncoder.
    // You can also define it as a @Bean in your security configuration for reuse.
    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    /**
     * Registers a new user by encrypting their password and saving them to the database.
     *
     * @param user the user to register
     * @return the saved user entity
     */
    public User registerUser(User user) {
        // Encrypt the user's password before saving
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    /**
     * Finds a user by their email.
     *
     * @param email the email of the user
     * @return the user entity if found, otherwise null
     */
    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
}

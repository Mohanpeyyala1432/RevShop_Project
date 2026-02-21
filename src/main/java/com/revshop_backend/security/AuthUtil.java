package com.revshop_backend.security;

import com.revshop_backend.model.User;
import com.revshop_backend.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
public class AuthUtil {

    private final UserRepository userRepository;

    public AuthUtil(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // Get the logged-in user from JWT
    public User getLoggedInUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String email = null;

        if (principal instanceof UserDetails) {
            email = ((UserDetails) principal).getUsername();
        }

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Logged-in user not found"));
    }
}
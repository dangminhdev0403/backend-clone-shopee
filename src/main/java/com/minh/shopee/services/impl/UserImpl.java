package com.minh.shopee.services.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.minh.shopee.domain.User;
import com.minh.shopee.repository.UserRepository;
import com.minh.shopee.services.UserService;
import com.minh.shopee.services.utils.error.DuplicateException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j(topic = "UserServiceImpl")
@RequiredArgsConstructor
@Service
public class UserImpl implements UserService {

    private final UserRepository userRepository;
    private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @SuppressWarnings("null")
    @Override
    public User createUser(User user) {
        log.info("Creating user with email: {}", user.getEmail());

        Optional<User> existingUser = this.userRepository.findByEmail(user.getEmail());
        if (existingUser.isPresent()) {
            log.warn("Duplicate user registration attempt for email: {}", user.getEmail());
            throw new DuplicateException(user.getEmail(), "already exists");
        }

        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }

        User savedUser = userRepository.save(user);
        log.info("User created successfully with email: {}", savedUser.getEmail());

        return savedUser;
    }

    @Override
    public <T> List<T> getListUser(Class<T> type) {
        log.debug("Fetching list of users with projection type: {}", type.getSimpleName());
        return this.userRepository.findAllBy(type);
    }

    @Override
    public User findByUsername(String username) {
        log.debug("Searching user by username: {}", username);

        Optional<User> user = this.userRepository.findByEmail(username);
        if (user.isPresent()) {
            log.info("User found with email: {}", username);
            return user.get();
        } else {
            log.warn("User not found with email: {}", username);
            throw new IllegalArgumentException("User not found");
        }
    }

    @Override
    public <T> T findByUsername(String username, Class<T> type) {
        log.debug("Searching user by username: {} with projection {}", username, type.getSimpleName());

        Optional<T> user = this.userRepository.findByEmail(username, type);
        if (user.isPresent()) {
            log.info("User found with email: {}", username);
            return user.get();
        } else {
            log.warn("User not found with email: {}", username);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
    }

    @Override
    @Transactional
    public void updateRefreshToken(String email, String refreshToken) {
        log.debug("Updating refresh token for user: {}", email);

        int isUpdated = this.userRepository.updateRefreshTokenByEmail(email, refreshToken);
        if (isUpdated == 0) {
            log.error("Failed to update refresh token - user not found: {}", email);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }

        log.info("Refresh token updated successfully for user: {}", email);
    }

    @Override
    public User findByEmailAndRefreshToken(String email, String refreshToken) {
        log.debug("Searching for user with email: {} and refresh token", email);

        Optional<User> user = this.userRepository.findByEmailAndRefreshToken(email, refreshToken);
        if (!user.isPresent()) {
            log.warn("User or refresh token not found for email: {}", email);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User or refresh token not found");
        }

        log.info("User found with valid refresh token: {}", email);
        return user.get();
    }

    @Override
    public <T> T findByEmailAndRefreshToken(String email, String refreshToken, Class<T> type) {
        log.debug("Searching for user with email: {} and refresh token using projection: {}", email,
                type.getSimpleName());

        Optional<T> user = this.userRepository.findByEmailAndRefreshToken(email, refreshToken, type);
        if (user.isPresent()) {
            log.info("User found with valid refresh token: {}", email);
            return user.get();
        }

        log.warn("User or refresh token not found for email: {}", email);
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User or refresh token not found");
    }

}

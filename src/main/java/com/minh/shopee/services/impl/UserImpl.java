package com.minh.shopee.services.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.minh.shopee.models.User;
import com.minh.shopee.repository.UserRepository;
import com.minh.shopee.services.UserService;
import com.minh.shopee.services.utils.error.DuplicateException;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class UserImpl implements UserService {

    private final UserRepository userRepository;
    private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @SuppressWarnings("null")
    @Override
    public User createUser(User user) {
        if (user == null) {
            throw new UsernameNotFoundException("User cannot be null");
        }

        Optional<User> existingUser = this.userRepository.findByEmail(user.getEmail());
        if (existingUser.isPresent()) {
            throw new DuplicateException(user.getEmail(), "already exists");
        }

        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));

        }

        return userRepository.save(user);
    }

    @Override
    public <T> List<T> getListUser(Class<T> type) {
        return this.userRepository.findAllBy(type);
    }

    @Override
    public User findByUsername(String username) {

        Optional<User> user = this.userRepository.findByEmail(username);
        if (user.isPresent()) {
            return user.get();
        } else {
            throw new IllegalArgumentException("User not found");
        }
    }

    @Override
    public <T> T findByUsername(String username, Class<T> type) {

        Optional<T> user = this.userRepository.findByEmail(username, type);

        if (user.isPresent()) {
            return user.get();
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
    }

    @Override
    @Transactional
    public void updateRefreshToken(String email, String refreshToken) {

        int isUpdated = this.userRepository.updateRefreshTokenByEmail(email, refreshToken);
        if (isUpdated == 0) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }

    }

    @Override
    public User findByEmailAndRefreshToken(String email, String refreshToken) {
        Optional<User> user = this.userRepository.findByEmailAndRefreshToken(email, refreshToken);
        if (!user.isPresent())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User or refresh token not found");

        return user.get();
    }

    @Override
    public <T> T findByEmailAndRefreshToken(String email, String refreshToken, Class<T> type) {
        Optional<T> user = this.userRepository.findByEmailAndRefreshToken(email, refreshToken, type);
        if (user.isPresent()) {
            return user.get();

        }

        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User or refresh token not found");
    }

}
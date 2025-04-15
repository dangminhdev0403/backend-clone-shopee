package com.minh.shopee.services.impl;

import java.util.List;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.minh.shopee.models.User;
import com.minh.shopee.repository.UserRepository;
import com.minh.shopee.services.UserService;

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
            throw new IllegalArgumentException("User cannot be null");
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

}
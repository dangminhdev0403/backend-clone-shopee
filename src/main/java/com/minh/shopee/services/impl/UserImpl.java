package com.minh.shopee.services.impl;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.minh.shopee.domain.dto.request.UserReqDTO;
import com.minh.shopee.domain.model.User;
import com.minh.shopee.repository.UserRepository;
import com.minh.shopee.services.UserService;
import com.minh.shopee.services.utils.error.DuplicateException;
import com.minh.shopee.services.utils.files.UploadCloud;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j(topic = "UserServiceImpl")
@RequiredArgsConstructor
@Service
public class UserImpl implements UserService {

    private final UserRepository userRepository;
    private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final UploadCloud uploadCloud;

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

    @Override
    public User updateProfile(String email, UserReqDTO userReqDTO, MultipartFile avatarFile) throws IOException {
        log.info("Update user request for email: {}", email);

        User userDb = this.findByUsername(email);

        log.debug("Current user data before update: name={}, email={}", userDb.getName(), userDb.getEmail());

        // Cập nhật tên nếu có giá trị
        Optional.ofNullable(userReqDTO.getName())
                .filter(StringUtils::hasText)
                .ifPresent(name -> {
                    log.info("Updating name from '{}' to '{}'", userDb.getName(), name);
                    userDb.setName(name);
                    log.info("Name updated successfully for user: {}", name);

                });

        // Cập nhật email nếu có và khác email hiện tại
        if (StringUtils.hasText(userReqDTO.getEmail()) && !userReqDTO.getEmail().equalsIgnoreCase(email)) {
            if (this.isExistEmail(userReqDTO.getEmail())) {
                log.warn("Attempt to update email to existing email: {}", userReqDTO.getEmail());
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email is already exist");
            }
            log.info("Updating email from '{}' to '{}'", userDb.getEmail(), userReqDTO.getEmail());
            userDb.setEmail(userReqDTO.getEmail());
            log.info("Email updated successfully for user: {}", userReqDTO.getEmail());

        }

        // Cập nhật password nếu có currentPassword và newPassword hợp lệ
        if (StringUtils.hasText(userReqDTO.getCurrentPassword()) && StringUtils.hasText(userReqDTO.getNewPassword())) {
            log.info("Attempting to update password for user: {}", email);
            boolean isValidPass = this.isCurrentPasswordValid(userReqDTO.getCurrentPassword(), userDb);
            if (!isValidPass) {
                log.warn("Invalid current password provided for user: {}", email);
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Current password is invalid");
            }
            userDb.setPassword(passwordEncoder.encode(userReqDTO.getNewPassword()));
            log.info("Password updated successfully for user: {}", email);
        }

        if (avatarFile != null && !avatarFile.isEmpty()) {
            if(userDb.getAvatarUrl() != null) {
             this.uploadCloud.deleteFile(userDb.getAvatarUrl());
            }
            String uploadUrl = this.uploadCloud.handleSaveUploadFile(avatarFile, "avatar");
            userDb.setAvatarUrl(uploadUrl);
        }

        User updatedUser = this.userRepository.save(userDb);

        log.info("User updated successfully: email={}, name={}", updatedUser.getEmail(), updatedUser.getName());

        return updatedUser;
    }

    public boolean isCurrentPasswordValid(String currentPasswordFromClient, User userDb) {
        return passwordEncoder.matches(currentPasswordFromClient, userDb.getPassword());
    }

    @Override
    public boolean isExistEmail(String email) {
        return this.userRepository.existsByEmail(email);
    }

}

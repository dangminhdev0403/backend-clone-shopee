package com.minh.shopee.services;

import java.util.List;

import com.minh.shopee.domain.dto.request.UserReqDTO;
import com.minh.shopee.domain.model.User;

public interface UserService {

    User createUser(User user);

    <T> List<T> getListUser(Class<T> type);

    User findByUsername(String username);

    <T> T findByUsername(String username, Class<T> type);

    void updateRefreshToken(String email, String refreshToken);

    User findByEmailAndRefreshToken(String email, String refreshToken);

    <T> T findByEmailAndRefreshToken(String email, String refreshToken, Class<T> type);

    User updateUser(String email, UserReqDTO userReqDTO);
    
    boolean isExistEmail(String email);
}
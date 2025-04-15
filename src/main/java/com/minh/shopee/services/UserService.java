package com.minh.shopee.services;

import java.util.List;

import com.minh.shopee.models.User;

public interface UserService {

    User createUser(User user);

    <T> List<T> getListUser(Class<T> type);
}
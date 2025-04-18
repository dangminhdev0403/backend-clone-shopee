package com.minh.shopee.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.minh.shopee.models.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {

    <T> List<T> findAllBy(Class<T> type);

    Optional<User> findByEmail(String email);

    <T> Optional<T> findByEmail(String email , Class<T> type);

    Optional<User> findByEmailAndRefreshToken(String email, String refreshToken);

}

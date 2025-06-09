package com.minh.shopee.domain.model;

import com.minh.shopee.domain.base.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
@Setter
@Table(name = "users")
@Entity
@AllArgsConstructor
@NoArgsConstructor

public class User extends BaseEntity {
    @NotBlank(message = "Email is required")
    private String email;

    @NotBlank(message = "Password is required")
    private String password;

    @Column(columnDefinition = "LONGTEXT")
    private String avatarUrl;

    @Column(columnDefinition = "LONGTEXT")
    private String refreshToken;

    @OneToOne(mappedBy = "user")
    private Cart cart;

}

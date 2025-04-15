package com.minh.shopee.models;

import com.minh.shopee.models.base.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
    @NotNull
    @NotBlank(message = "Name is required")
    private String email;

    @NotNull
    @NotBlank(message = "Password is required")
    private String password;


    @Column(columnDefinition = "LONGTEXT")
    private String refreshToken;
}

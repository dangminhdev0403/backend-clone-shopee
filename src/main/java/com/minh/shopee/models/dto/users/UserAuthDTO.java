package com.minh.shopee.models.dto.users;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UserAuthDTO {
    private String email;
    private String password;
}

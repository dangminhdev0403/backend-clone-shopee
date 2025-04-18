package com.minh.shopee.models.dto.users;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UserRefreshToken {
    private long id;
    private String refreshToken;
}

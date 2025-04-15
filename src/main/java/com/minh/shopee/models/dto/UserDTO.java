package com.minh.shopee.models.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
//! Không thêm contructoer không thâm số
//! tên và kiểu dữ liệu phải trùng với bảng gốc
public class UserDTO {
    private String name;
    private String email;

}

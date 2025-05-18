package com.minh.shopee.controllers.admin;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.minh.shopee.domain.dto.users.UserDTO;
import com.minh.shopee.domain.model.User;
import com.minh.shopee.services.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("")
    public ResponseEntity<User> createUser(@RequestBody User user) {
        User userCreated = userService.createUser(user);
        return ResponseEntity.ok().body(userCreated);

    }

    @GetMapping("")
    public ResponseEntity<List<UserDTO>> getListUser() {

        List<UserDTO> users = userService.getListUser(UserDTO.class);
        return ResponseEntity.ok().body(users);
    }

}

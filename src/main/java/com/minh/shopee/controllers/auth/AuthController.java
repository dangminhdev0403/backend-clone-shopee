package com.minh.shopee.controllers.auth;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.minh.shopee.models.User;
import com.minh.shopee.models.anotation.ApiDescription;
import com.minh.shopee.models.dto.ResLoginDTO;
import com.minh.shopee.models.request.LoginRequest;
import com.minh.shopee.services.UserService;
import com.minh.shopee.services.utils.SecurityUtils;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UserService userService;
    private final SecurityUtils securityUtils;

    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    @Value("${minh.jwt.refresh-token.validity.in.seconds}")
    private long refreshTokenExpiration;

    @PostMapping("/login")
    @ApiDescription("API Login")
    public ResponseEntity<ResLoginDTO> getMethodName(@RequestBody @Valid LoginRequest userRequest) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                userRequest.getEmail(), userRequest.getPassword());

        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        User currentUser = this.userService.findByUsername(userRequest.getEmail());

        String email = currentUser.getEmail();
        String name = currentUser.getName();
        ResLoginDTO.UserLogin userLogin = ResLoginDTO.UserLogin.builder().email(email).name(name).build();

        // Tạo accessToken token
        String accessToken = this.securityUtils.createAccessToken(userRequest.getEmail(), userLogin);
        ResLoginDTO resLoginDTO = ResLoginDTO.builder().accessToken(accessToken).user(userLogin).build();

        // ! Nạp thông tin hoi vào SecurityContextHolder
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // ! Tạo refresh_token
        String refreshToken = this.securityUtils.createRefreshToken(userRequest.getEmail(), resLoginDTO);
        this.userService.updateRefreshToken(userRequest.getEmail(), refreshToken);
        // ! Lưu refreshToken với cookie
        ResponseCookie cookie = ResponseCookie.from("refresh_token",
                refreshToken)
                .httpOnly(true)
                .path("/")
                .maxAge(refreshTokenExpiration).build();
        return ResponseEntity.ok().header("Set-Cookie", cookie.toString()).body(resLoginDTO);

    }

}

package com.minh.shopee.controllers.auth;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.minh.shopee.models.User;
import com.minh.shopee.models.anotation.ApiDescription;
import com.minh.shopee.models.dto.ResLoginDTO;
import com.minh.shopee.models.dto.users.UserDTO;
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
    private static final String REFRESH_TOKEN = "refresh_token";
    private static final String HEADER_NAME = "Set-Cookie";

    private Map<String, Instant> refreshTokensRequestTime = new ConcurrentHashMap<>();

    private Duration refreshTokenAccessRequestExpiration = Duration.ofSeconds(3);

    @Value("${minh.jwt.refresh-token.validity.in.seconds}")
    private long refreshTokenExpiration;

    @PostMapping("/login")
    @ApiDescription("API Login")
    public ResponseEntity<ResLoginDTO> login(@RequestBody @Valid LoginRequest userRequest) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                userRequest.getEmail(), userRequest.getPassword());

        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        UserDTO currentUser = this.userService.findByUsername(userRequest.getEmail() , UserDTO.class);

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
        ResponseCookie cookie = ResponseCookie.from(
                REFRESH_TOKEN,
                refreshToken)
                .httpOnly(true)
                .path("/")
                .maxAge(refreshTokenExpiration).build();
        return ResponseEntity.ok().header(HEADER_NAME, cookie.toString()).body(resLoginDTO);

    }

    @GetMapping("/refresh")
    @ApiDescription("Refresh token")
    public ResponseEntity<ResLoginDTO> refreshToken(
            @CookieValue(name = "refresh_token", required = false) Optional<String> refreshToken) {

        if (refreshToken.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Refresh token not found");
        }

        String newRefreshToken = refreshToken.get();
        // ! chống spam
        // ? lấy thời gian gọi request
        Instant lastRequestTime = this.refreshTokensRequestTime.get(refreshToken.get());
        if (lastRequestTime != null && Instant.now().isBefore(lastRequestTime.plus(refreshTokenAccessRequestExpiration))) {
            throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "Quá nhiều lần gọi request");

        }

        this.refreshTokensRequestTime.put(refreshToken.get(), Instant.now());

        Jwt decodedToken = this.securityUtils.checkValidRefreshToken(refreshToken.get()); // ! Token được giải

        String email = decodedToken.getSubject();

        // ! Tìm User chứa refresh token tương ứng trong database
        User currentUser = this.userService.findByEmailAndRefreshToken(email, refreshToken.get());

        // ! Tạo access_token
        ResLoginDTO.UserLogin userLogin = ResLoginDTO.UserLogin.builder().email(email).name(currentUser.getName())
                .build();
        String accessToken = this.securityUtils.createAccessToken(email, userLogin);
        ResLoginDTO resLoginDTO = ResLoginDTO.builder().user(userLogin).accessToken(accessToken).build();

        Instant expiresAt = decodedToken.getExpiresAt();
        Instant now = Instant.now();
        Instant beforeexpiresAt = now.plus(5, ChronoUnit.MINUTES);

        // ! kiểm tra refresh token hết hạn hay chưa rồi mới tạo refresh token
        if (expiresAt != null && beforeexpiresAt.isAfter(expiresAt)) {

            newRefreshToken = this.securityUtils.createRefreshToken(email, resLoginDTO);

            this.userService.updateRefreshToken(email, newRefreshToken);
        }

        ResponseCookie cookie = ResponseCookie.from(
                REFRESH_TOKEN,
                newRefreshToken)
                .httpOnly(true)
                .path("/")
                .maxAge(refreshTokenExpiration).build();
        return ResponseEntity.ok().header(HEADER_NAME, cookie.toString()).body(resLoginDTO);
    }

    @PostMapping("/logout")
    @ApiDescription("Logout")
    public ResponseEntity<String> logout(
            @CookieValue(name = REFRESH_TOKEN, required = false) Optional<String> refreshToken) {

        if (refreshToken.isEmpty()) {

            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy refresh token");

        }

        // ! Kiem tra refresh token
        Jwt decodedToken = this.securityUtils.checkValidRefreshToken(refreshToken.get()); // ! Token được giải

        String email = decodedToken.getSubject();

        // ! Tìm refresh token trong database

        this.userService.updateRefreshToken(email, null);

        ResponseCookie cookie = ResponseCookie.from(
                REFRESH_TOKEN,
                "")
                .httpOnly(true)
                .path("/")
                .maxAge(refreshTokenExpiration).build();

        return ResponseEntity.ok().header(HEADER_NAME, cookie.toString()).body("Đăng xuất thành công");

    }

    @PostMapping("/register")
    @ApiDescription("API Register")
    public ResponseEntity<ResLoginDTO> register(@RequestBody @Valid User userRequest) {

        User createUser = this.userService.createUser(userRequest);

        String email = createUser.getEmail();
        String name = createUser.getName();
        ResLoginDTO.UserLogin userLogin = ResLoginDTO.UserLogin.builder().email(email).name(name).build();

        // Tạo accessToken token
        String accessToken = this.securityUtils.createAccessToken(userRequest.getEmail(), userLogin);
        ResLoginDTO resLoginDTO = ResLoginDTO.builder().accessToken(accessToken).user(userLogin).build();

        // ! Tạo refresh_token
        String refreshToken = this.securityUtils.createRefreshToken(userRequest.getEmail(), resLoginDTO);
        this.userService.updateRefreshToken(userRequest.getEmail(), refreshToken);
        // ! Lưu refreshToken với cookie
        ResponseCookie cookie = ResponseCookie.from(
                REFRESH_TOKEN,
                refreshToken)
                .httpOnly(true)
                .path("/")
                .maxAge(refreshTokenExpiration).build();
        return ResponseEntity.ok().header(HEADER_NAME, cookie.toString()).body(resLoginDTO);

    }

}

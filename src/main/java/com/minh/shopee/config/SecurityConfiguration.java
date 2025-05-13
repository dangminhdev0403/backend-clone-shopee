package com.minh.shopee.config;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfiguration {

        @Bean
        public SecurityFilterChain filterChain(HttpSecurity http,
                        CustomAuthenticationEntryPoint customAuthenticationEntryPoint)
                        throws Exception {
                List<String> versions = List.of("v1", "v2", "v3");
                String apiBase = "/api";
                // Các path công khai cho từng version
                String[] versionedPaths = { "/categories/**", "/posts/**" };
                // Các path chung (không version)
                String[] commonPaths = { "/auth/**", "/swagger-ui/**", "/api-docs/**" };

                // Gộp path công khai cho tất cả version
                List<String> versionedWhitelist = versions.stream()
                                .flatMap(version -> Arrays.stream(versionedPaths)
                                                .map(path -> apiBase + "/" + version + path))
                                .toList();
                // Gộp tất cả lại
                String[] whiteList = Stream.concat(
                                versionedWhitelist.stream(),
                                Arrays.stream(commonPaths)).toArray(String[]::new);

                http
                                .csrf(c -> c.disable())
                                .cors(Customizer.withDefaults())
                                .authorizeHttpRequests(authz ->
                                // prettier-ignore
                                authz.requestMatchers(whiteList)
                                                .permitAll()
                                                .anyRequest().authenticated())
                                .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults())
                                                .authenticationEntryPoint(customAuthenticationEntryPoint))
                                .formLogin(f -> f.disable())
                                .sessionManagement(session -> session
                                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

                return http.build();
        }
}

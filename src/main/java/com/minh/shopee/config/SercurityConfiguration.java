package com.minh.shopee.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SercurityConfiguration {

        @Bean
        public SecurityFilterChain filterChain(HttpSecurity http,
                        CustomAuthenticationEntryPoint customAuthenticationEntryPoint)
                        throws Exception {

                String whileTrue[] = { "/auth/**" };
                http
                                .csrf(c -> c.disable())
                                .authorizeHttpRequests(authz ->
                                // prettier-ignore

                                authz
                                                .requestMatchers(whileTrue).permitAll()
                                                .anyRequest().authenticated()

                                )
                                .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults())
                                                .authenticationEntryPoint(customAuthenticationEntryPoint))
                                .formLogin(f -> f.disable())
                                .sessionManagement(session -> session
                                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

                return http.build();
        }
}

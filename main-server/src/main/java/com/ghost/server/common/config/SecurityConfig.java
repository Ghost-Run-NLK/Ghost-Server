package com.ghost.server.common.config;

import com.ghost.server.common.security.JwtAuthenticationFilter;
import com.ghost.server.common.security.JwtProperties;
import com.ghost.server.common.security.JwtTokenProvider;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

// TODO: Admin 권한 가드는 별도 PR — 현재는 인증만 요구하고 ADMIN role 도입 시 hasRole("ADMIN")로 교체
@Configuration
@EnableConfigurationProperties(JwtProperties.class)
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   JwtTokenProvider jwtTokenProvider) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .anonymous(AbstractHttpConfigurer::disable)
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // Swagger
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        // Auth endpoints
                        .requestMatchers("/api/v1/auth/**").permitAll()
                        // 게스트 허용 read 경로
                        .requestMatchers(HttpMethod.GET, "/api/v1/courses").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/courses/*").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/courses/*/leaderboard").permitAll()
                        // /api/v1/runs/{runId} — public ID prefix "run_" 로만 게스트 허용 (active 등은 인증 필수)
                        .requestMatchers(HttpMethod.GET, "/api/v1/runs/run_*").permitAll()
                        // 그 외 전부 인증 필수 (admin 포함)
                        .anyRequest().authenticated()
                )
                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider),
                        UsernamePasswordAuthenticationFilter.class)
                .build();
    }
}

package com.damm.server.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // CSRF 비활성화 (REST API 서버이므로 불필요)
                .csrf(AbstractHttpConfigurer::disable)

                // Form 로그인, Basic HTTP 인증 비활성화 (JWT/OAuth2를 쓸 예정이므로)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)

                // 세션 관리 상태를 STATELESS로 설정 (JWT 사용을 위한 기본 세팅)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // 권한 규칙 설정
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(
                                "/docs",
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-resources/**"
                        ).permitAll()

                        // TODO 개발 초기 단계이므로 일단 모든 API 경로를 열어 두고 나중에 인증이 필요한 곳만 .authenticated()로 잠글 예정.
                        .requestMatchers("/api/**").permitAll()

                        // 그 외의 모든 요청은 인증 필요
                        .anyRequest().authenticated()
                );

        return http.build();
    }
}
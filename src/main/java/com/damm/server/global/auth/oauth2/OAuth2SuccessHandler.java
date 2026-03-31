package com.damm.server.global.auth.oauth2;

import com.damm.server.global.auth.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    @Value("${app.oauth2.authorized-redirect-uri}")
    private String redirectUri;

    // JWT 토큰을 만들어줄 클래스
     private final JwtProvider jwtProvider;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

        // 1. 권한(Role) 추출
        // 사용자가 가진 권한 목록 중 첫 번째 값을 가져온다.
        String role = authentication.getAuthorities().stream()
                .findFirst()
                .map(GrantedAuthority::getAuthority)
                .orElse("ROLE_USER");

        // 2. 이메일 추출
        // 제공자(카카오, 구글, 네이버 등)마다 속성 구조가 다르므로 안전하게 파싱.
        String email = extractEmail(oAuth2User.getAttributes());

        // 3. JWT 토큰 발급
        String accessToken = jwtProvider.createAccessToken(email, role);
        log.info("로그인 성공! 발급된 Access Token: {}", accessToken);

        // 4. 프론트엔드로 리다이렉트
        String targetUrl = UriComponentsBuilder.fromUriString(redirectUri)
                .queryParam("token", accessToken)
                .build().toUriString();

        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

    private String extractEmail(Map<String, Object> attributes) {
        // 구글: 최상단에 email 존재
        if (attributes.containsKey("email")) {
            return (String) attributes.get("email");
        }
        // 카카오: kakao_account 객체 안에 숨어있음
        if (attributes.containsKey("kakao_account")) {
            Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
            return (String) kakaoAccount.get("email");
        }
        // 네이버: response 객체 안에 존재
        if (attributes.containsKey("response")) {
            Map<String, Object> response = (Map<String, Object>) attributes.get("response");
            return (String) response.get("email");
        }
        throw new IllegalArgumentException("소셜 응답에서 이메일 정보를 찾을 수 없습니다.");
    }
}
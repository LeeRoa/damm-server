package com.damm.server.global.auth.oauth2;

import com.damm.server.global.auth.oauth2.info.OAuth2UserInfo;
import com.damm.server.global.auth.oauth2.info.impl.GoogleOAuth2UserInfo;
import com.damm.server.global.auth.oauth2.info.impl.KakaoOAuth2UserInfo;
import com.damm.server.global.auth.oauth2.info.impl.NaverOAuth2UserInfo;
import com.damm.server.modules.member.domain.Member;
import com.damm.server.modules.member.domain.enums.Role;
import lombok.Builder;
import lombok.Getter;

import java.util.Map;

@Getter
public class OAuthAttributes {

    private final String nameAttributeKey; // OAuth2 로그인 진행 시 키가 되는 필드 값 (PK)
    private final OAuth2UserInfo oauth2UserInfo; // 다형성 활용 (구글, 카카오 모두 수용)

    @Builder
    private OAuthAttributes(String nameAttributeKey, OAuth2UserInfo oauth2UserInfo) {
        this.nameAttributeKey = nameAttributeKey;
        this.oauth2UserInfo = oauth2UserInfo;
    }

    // 어떤 플랫폼인지 확인하고 알맞은 객체를 생성해 반환하는 팩토리 메서드
    public static OAuthAttributes of(String registrationId, String userNameAttributeName, Map<String, Object> attributes) {

        if ("kakao".equals(registrationId)) {
            return ofKakao(userNameAttributeName, attributes);
        }
        if ("naver".equals(registrationId)) {
            return ofNaver("id", attributes);
        }
        if ("google".equals(registrationId)) {
            return ofGoogle(userNameAttributeName, attributes);
        }

        throw new IllegalArgumentException("지원하지 않는 소셜 로그인입니다: " + registrationId);
    }

    private static OAuthAttributes ofGoogle(String userNameAttributeName, Map<String, Object> attributes) {
        return OAuthAttributes.builder()
                .nameAttributeKey(userNameAttributeName)
                .oauth2UserInfo(new GoogleOAuth2UserInfo(attributes))
                .build();
    }

    private static OAuthAttributes ofKakao(String userNameAttributeName, Map<String, Object> attributes) {
        return OAuthAttributes.builder()
                .nameAttributeKey(userNameAttributeName)
                .oauth2UserInfo(new KakaoOAuth2UserInfo(attributes))
                .build();
    }

    private static OAuthAttributes ofNaver(String userNameAttributeName, Map<String, Object> attributes) {
        return OAuthAttributes.builder()
                .nameAttributeKey(userNameAttributeName)
                .oauth2UserInfo(new NaverOAuth2UserInfo(attributes))
                .build();
    }

    // 편의 메서드: 소셜 정보가 DB에 없을 때, 신규 회원가입을 위한 Member 엔티티 자동 생성
    public Member toEntity() {
        return Member.builder()
                .email(oauth2UserInfo.getEmail())
                .nickname(oauth2UserInfo.getNickname())
                .profileImageUrl(oauth2UserInfo.getProfileImageUrl())
                .provider(oauth2UserInfo.getProvider())
                .providerId(oauth2UserInfo.getProviderId())
                .role(Role.USER) // 가입 시 기본 권한은 USER
                .build();
    }
}
package com.damm.server.global.auth.oauth2.info.impl;

import com.damm.server.global.auth.oauth2.info.OAuth2UserInfo;
import com.damm.server.modules.member.domain.enums.Provider;

import java.util.Map;

public class KakaoOAuth2UserInfo implements OAuth2UserInfo {

    private final Map<String, Object> attributes; // 카카오에서 받은 전체 JSON

    public KakaoOAuth2UserInfo(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public Provider getProvider() {
        return Provider.KAKAO;
    }

    @Override
    public String getProviderId() {
        return String.valueOf(attributes.get("id")); // 카카오는 Long 타입으로 id를 줍니다.
    }

    @Override
    public String getEmail() {
        Map<String, Object> account = (Map<String, Object>) attributes.get("kakao_account");
        String email = (account != null) ? (String) account.get("email") : null;

        // 이메일이 없으면 카카오 고유 번호를 활용해 유니크한 식별용 메일을 만듭니다.
        if (email == null || email.isBlank()) {
            return "kakao_" + getProviderId() + "@damm.com";
        }
        return email;
    }

    @Override
    public String getNickname() {
        // 닉네임은 kakao_account -> profile 맵 안에 또 숨어있습니다.
        Map<String, Object> account = (Map<String, Object>) attributes.get("kakao_account");
        if (account == null) return null;

        Map<String, Object> profile = (Map<String, Object>) account.get("profile");
        if (profile == null) return null;

        return (String) profile.get("nickname");
    }

    @Override
    public String getProfileImageUrl() {
        Map<String, Object> account = (Map<String, Object>) attributes.get("kakao_account");
        if (account == null) return null;

        Map<String, Object> profile = (Map<String, Object>) account.get("profile");
        if (profile == null) return null;

        return (String) profile.get("thumbnail_image_url"); // 해상도가 낮은 썸네일 기준 (필요시 profile_image_url 사용)
    }
}
package com.damm.server.global.auth.oauth2.info;

import com.damm.server.modules.member.domain.enums.Provider;

import java.util.Map;

public interface OAuth2UserInfo {
    Map<String, Object> getAttributes(); // 원본 데이터
    Provider getProvider();              // KAKAO, GOOGLE 등
    String getProviderId();              // 플랫폼 고유 식별자(PK)
    String getEmail();
    String getNickname();
    String getProfileImageUrl();
}
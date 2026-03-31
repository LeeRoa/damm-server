package com.damm.server.global.auth.oauth2.info.impl;

import com.damm.server.global.auth.oauth2.info.OAuth2UserInfo;
import com.damm.server.modules.member.domain.enums.Provider;

import java.util.Map;

public class NaverOAuth2UserInfo implements OAuth2UserInfo {

    private final Map<String, Object> attributes;
    private final Map<String, Object> response;

    public NaverOAuth2UserInfo(Map<String, Object> attributes) {
        this.attributes = attributes;
        // 생성될 때 미리 response 객체를 파싱.
        this.response = (Map<String, Object>) attributes.get("response");
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public Provider getProvider() {
        return Provider.NAVER;
    }

    @Override
    public String getProviderId() {
        return (String) response.get("id");
    }

    @Override
    public String getEmail() {
        return (String) response.get("email");
    }

    @Override
    public String getNickname() {
        return (String) response.get("nickname");
    }

    @Override
    public String getProfileImageUrl() {
        return (String) response.get("profile_image");
    }
}
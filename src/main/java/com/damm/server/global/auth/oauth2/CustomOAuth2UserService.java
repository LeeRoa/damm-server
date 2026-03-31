package com.damm.server.global.auth.oauth2;

import com.damm.server.modules.member.domain.Member;
import com.damm.server.modules.member.domain.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final MemberRepository memberRepository;

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) {
        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        String userNameAttributeName = userRequest.getClientRegistration().getProviderDetails()
                .getUserInfoEndpoint().getUserNameAttributeName();

        OAuthAttributes attributes = OAuthAttributes.of(registrationId, userNameAttributeName, oAuth2User.getAttributes());
        Member member = saveOrUpdate(attributes);

        // 네이버 'id' Null 오류 해결을 위해, 네이버일 경우 'response' 내부 맵을 넘겨줍니다.
        Map<String, Object> memberAttributes = attributes.getOauth2UserInfo().getAttributes();
        if ("naver".equals(registrationId)) {
            memberAttributes = (Map<String, Object>) memberAttributes.get("response");
        }

        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority(member.getRole().getKey())),
                memberAttributes,
                attributes.getNameAttributeKey()
        );
    }

    /**
     * 유저가 이미 DB에 있으면 정보를 갱신하고, 없으면 새로 가입시키는 핵심 로직
     */
    private Member saveOrUpdate(OAuthAttributes attributes) {
        Member member = memberRepository.findByEmail(attributes.getOauth2UserInfo().getEmail())
                .map(entity -> {
                    log.info("기존 유저 로그인: {}", entity.getEmail());
                    // 프로필 사진이나 닉네임이 바뀌었을 경우를 대비해 업데이트해 줍니다.
                    entity.updateProfile(
                            attributes.getOauth2UserInfo().getNickname(),
                            attributes.getOauth2UserInfo().getProfileImageUrl()
                    );
                    return entity;
                })
                .orElseGet(() -> {
                    log.info("신규 유저 가입: {}", attributes.getOauth2UserInfo().getEmail());
                    // 처음 온 유저라면 우리가 만들어둔 편의 메서드로 엔티티를 바로 찍어냅니다.
                    return attributes.toEntity();
                });

        return memberRepository.save(member);
    }
}
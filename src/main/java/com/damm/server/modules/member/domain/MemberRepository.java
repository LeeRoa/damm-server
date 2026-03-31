package com.damm.server.modules.member.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    // 소셜 로그인 시 이미 가입된 사용자인지 이메일로 확인
    Optional<Member> findByEmail(String email);

}
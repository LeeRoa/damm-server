package com.damm.server.modules.member.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Member", description = "회원 관련 API")
@RestController
@RequestMapping("/api/v1/members")
public class MemberController {

    @Operation(summary = "내 정보 조회", description = "토큰을 이용해 현재 로그인한 유저 정보를 가져옵니다.")
    @GetMapping("/me")
    public String getMyInfo(@AuthenticationPrincipal UserDetails userDetails) {
        // 인증이 정상적으로 되었다면 userDetails에 이메일 정보가 들어있습니다.
        return "안녕하세요, " + userDetails.getUsername() + "님! 인증에 성공하셨습니다.";
    }
}
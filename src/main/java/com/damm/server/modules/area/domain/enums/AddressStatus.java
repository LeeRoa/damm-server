package com.damm.server.modules.area.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AddressStatus {
    PENDING("처리 대기"),   // API에서 갓 가져온 상태
    SUCCESS("보정 완료"),   // 지오코딩 성공하여 표준 주소 채워짐
    FAIL("보정 실패"),      // 지오코딩 실패 (수동 확인 필요)
    MANUAL("수동 보정");    // 관리자가 직접 확인 후 수정한 상태

    private final String description;
}
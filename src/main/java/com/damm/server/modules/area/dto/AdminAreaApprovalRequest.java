package com.damm.server.modules.area.dto;

import com.damm.server.modules.area.domain.enums.AreaStatus;
import com.damm.server.modules.area.domain.enums.AreaType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AdminAreaApprovalRequest(
        @NotBlank(message = "장소 이름은 필수입니다.")
        String areaNm,

        @NotBlank(message = "주소는 필수입니다.")
        String address,

        @NotNull(message = "구역 타입은 필수입니다.")
        AreaType areaSe,

        @NotNull(message = "변경할 상태를 지정해주세요 (VERIFIED 또는 CLOSED 등)")
        AreaStatus status
) {}
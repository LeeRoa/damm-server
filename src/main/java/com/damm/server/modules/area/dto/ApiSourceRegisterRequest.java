package com.damm.server.modules.area.dto;

import com.damm.server.infra.publicdata.domain.enums.District;
import com.damm.server.infra.publicdata.domain.enums.Province;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.URL;

public record ApiSourceRegisterRequest(
        @Schema(description = "시/도", implementation = Province.class)
        Province province,

        @Schema(description = "시/군/구", implementation = District.class)
        District cityDistrict,

        @NotBlank(message = "API 주소는 필수 입력값입니다.")
        @URL(message = "올바른 URL 형식이 아닙니다. (예: https://api.odcloud.kr/...)")
        @Schema(description = "공공데이터 API 주소")
        String baseUrl
) {}
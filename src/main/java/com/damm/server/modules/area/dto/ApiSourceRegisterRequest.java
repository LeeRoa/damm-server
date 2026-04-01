package com.damm.server.modules.area.dto;

import com.damm.server.infra.publicdata.domain.enums.ParserType;
import com.damm.server.infra.publicdata.domain.enums.Province;

public record ApiSourceRegisterRequest(
        Province province,
        String cityDistrict,
        String baseUrl,
        ParserType parserType
) {}
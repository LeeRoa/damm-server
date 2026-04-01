package com.damm.server.modules.area.dto;

import com.damm.server.infra.publicdata.domain.enums.ParserType;

public record ApiSourceRegisterRequest(
        String regionName,
        String baseUrl,
        ParserType parserType
) {}
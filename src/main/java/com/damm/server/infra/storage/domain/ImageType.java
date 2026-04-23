package com.damm.server.infra.storage.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ImageType {
    AREA,
    PROFILE,
    REVIEW,
    COMMON
}
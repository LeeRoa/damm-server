package com.damm.server.infra.publicdata.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record PublicDataMeta(
        @JsonProperty("totalCount") Integer totalCount,
        @JsonProperty("pageNo") String pageNo,
        @JsonProperty("numOfRows") String numOfRows,
        @JsonProperty("resultCode") String resultCode,
        @JsonProperty("resultMsg") String resultMsg
) {
    public PublicDataMeta(Integer totalCount, String pageNo, String numOfRows) {
        this(totalCount, pageNo, numOfRows, "OK", "SUCCESS");
    }
}
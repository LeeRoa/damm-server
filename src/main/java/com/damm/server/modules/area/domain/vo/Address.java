package com.damm.server.modules.area.domain.vo;

import com.damm.server.infra.kakao.dto.GeocodingResponse;
import com.damm.server.infra.publicdata.domain.enums.District;
import com.damm.server.infra.publicdata.domain.enums.Province;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Address {

    // API에서 넘어온 가공되지 않은 주소
    @Column(length = 500)
    private String rawAddress;

    /**
     * API 항목: rdnmadr (소재지 도로명주소)
     */
    @Column(length = 200)
    private String rdnmadr;

    /**
     * API 항목: lnmadr (소재지 지번주소)
     */
    @Column(length = 200)
    private String lnmadr;

    /**
     * API 항목: ctprvnnm (시도명)
     */
    @Column(length = 40)
    private Province province;

    /**
     * API 항목: signgunm (시군구명)
     */
    @Column(length = 40)
    private District district;

    /**
     * API 항목: emdnm (읍면동명)
     */
    @Column(length = 40)
    private String emdnm;

    @Builder
    public Address(String rawAddress) {
        this.rawAddress = rawAddress;
    }

    /**
     * 지오코딩 결과를 바탕으로 누락된 주소 정보를 보정한다.
     */
    public void updateByGeocoding(GeocodingResponse res) {
        this.rdnmadr = res.rdnmadr();
        this.lnmadr = res.lnmadr();
        this.province = res.province();
        this.district = res.district();
        this.emdnm = res.emdnm();
    }

    public String getFullRoadAddress() {
        // 만약 이미 풀 주소라면 그대로 반환, 아니라면 조합
        if (this.getRdnmadr().startsWith(this.getProvince().getKoreanName())) {
            return this.getRdnmadr();
        }

        return String.format("%s %s %s",
                this.getProvince().getKoreanName(),
                this.getDistrict(),
                this.getRdnmadr());
    }
}
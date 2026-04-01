package com.damm.server.modules.area.mapper;

import com.damm.server.infra.publicdata.dto.SmokingAreaItem;
import com.damm.server.modules.area.domain.SmokingArea;
import com.damm.server.modules.area.domain.enums.AreaStatus;
import com.damm.server.modules.area.domain.enums.AreaType;
import com.damm.server.modules.area.domain.vo.Address;
import com.damm.server.modules.area.domain.vo.Coordinate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SmokingAreaMapper {

    public SmokingArea toEntity(SmokingAreaItem item) {
        Double lat = parseDouble(item.latitude());
        Double lng = parseDouble(item.longitude());

        Coordinate coordinate = new Coordinate(lat, lng);

        Address addressVo = Address.builder()
                .ctprvnnm(item.ctprvnnm())
                .signgunm(item.signgunm())
                .emdnm(item.emdnm())
                .rdnmadr(item.rdnmadr())
                .lnmadr(item.lnmadr())
                .build();

        return SmokingArea.builder()
                .id(item.id())
                .areaNm(item.areaNm() != null ? item.areaNm() : "이름 없음")
                .areaDesc(item.areaDesc())
                .coordinate(coordinate)
                .address(addressVo)
                .areaAr(parseDouble(item.areaAr()))
                .fcltyKnd(item.fcltyKnd())
                .instNm(item.instNm())
                .areaSe(item.areaSe() != null ? AreaType.from(item.areaSe()) : AreaType.GENERAL)
                .status(AreaStatus.VERIFIED)
                .refDate(item.refDate())
                .build();
    }

    private Double parseDouble(String val) {
        if (val == null || val.isBlank()) return 0.0;
        try {
            return Double.parseDouble(val.replaceAll("[^0-9.-]", ""));
        } catch (Exception e) {
            return 0.0;
        }
    }
}
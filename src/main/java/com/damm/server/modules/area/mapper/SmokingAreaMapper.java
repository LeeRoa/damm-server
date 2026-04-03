package com.damm.server.modules.area.mapper;

import com.damm.server.global.util.AddressUtils;
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
        Double lat = parseDouble(item.get(SmokingAreaItem.위도));
        Double lng = parseDouble(item.get(SmokingAreaItem.경도));

        Coordinate coordinate = new Coordinate(lat, lng);

        Address addressVo = Address.builder()
                .rawAddress(AddressUtils.refineForGeocoding(item.getAssembledAddress()))
                .build();

        return SmokingArea.builder()
                .id(item.get(SmokingAreaItem.KEY_ID))
                .areaNm(item.getOrDefault(SmokingAreaItem.흡연구역_명칭, "이름 없음"))
                .areaDesc(item.get(SmokingAreaItem.설치_위치_상세))
                .coordinate(coordinate)
                .address(addressVo)
                .areaAr(parseDouble(item.get(SmokingAreaItem.면적)))
                .fcltyKnd(item.get(SmokingAreaItem.시설_구분))
                .instNm(item.get(SmokingAreaItem.관리_기관_명칭))
                .areaSe(item.get(SmokingAreaItem.흡연구역_구분) != null && !item.get(SmokingAreaItem.흡연구역_구분).isBlank()
                        ? AreaType.from(item.get(SmokingAreaItem.흡연구역_구분))
                        : AreaType.GENERAL)
                .status(AreaStatus.VERIFIED)
                .refDate(item.get(SmokingAreaItem.데이터_기준_일자))
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
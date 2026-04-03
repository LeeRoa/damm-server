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
        Double lat = parseDouble(item.get(SmokingAreaItem.KEY_LATITUDE));
        Double lng = parseDouble(item.get(SmokingAreaItem.KEY_LONGITUDE));

        Coordinate coordinate = new Coordinate(lat, lng);

        Address addressVo = Address.builder()
                .province(item.toProvince())
                .signgunm(item.get(SmokingAreaItem.KEY_SIGNGUNM))
                .emdnm(item.get(SmokingAreaItem.KEY_EMDNM))
                .rdnmadr(item.get(SmokingAreaItem.KEY_RDNMADR))
                .lnmadr(item.get(SmokingAreaItem.KEY_LNMADR))
                .build();

        return SmokingArea.builder()
                .id(item.get(SmokingAreaItem.KEY_ID))
                .areaNm(item.getOrDefault(SmokingAreaItem.KEY_AREA_NM, "이름 없음"))
                .areaDesc(item.get(SmokingAreaItem.KEY_AREA_DESC))
                .coordinate(coordinate)
                .address(addressVo)
                .areaAr(parseDouble(item.get(SmokingAreaItem.KEY_AREA_AR)))
                .fcltyKnd(item.get(SmokingAreaItem.KEY_FCLTY_KND))
                .instNm(item.get(SmokingAreaItem.KEY_INST_NM))
                .areaSe(item.get(SmokingAreaItem.KEY_AREA_SE) != null && !item.get(SmokingAreaItem.KEY_AREA_SE).isBlank()
                        ? AreaType.from(item.get(SmokingAreaItem.KEY_AREA_SE))
                        : AreaType.GENERAL)
                .status(AreaStatus.VERIFIED)
                .refDate(item.get(SmokingAreaItem.KEY_REF_DATE))
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
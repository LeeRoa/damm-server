package com.damm.server.modules.area.mapper;

import com.damm.server.global.util.AddressUtil;
import com.damm.server.infra.publicdata.dto.SmokingAreaItem;
import com.damm.server.modules.area.domain.SmokingArea;
import com.damm.server.modules.area.domain.enums.AreaStatus;
import com.damm.server.modules.area.domain.enums.AreaType;
import com.damm.server.modules.area.domain.vo.Address;
import com.damm.server.modules.area.domain.vo.Coordinate;
import com.damm.server.modules.area.dto.SmokingAreaSuggestRequest;
import lombok.extern.slf4j.Slf4j;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * 흡연구역 관련 데이터를 엔티티로 변환하는 매퍼 클래스
 * 공공데이터 API 응답 데이터와 사용자 제보 DTO를 도메인 모델(SmokingArea)로 매핑한다.
 */
@Slf4j
@Component
public class SmokingAreaMapper {

    // PostGIS 공간 데이터(SRID 4326, WGS84 좌표계)를 생성하기 위한 팩토리
    private final GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);

    /**
     * 공공데이터 포털 API 응답 항목(SmokingAreaItem)을 엔티티로 변환
     * @param item API 한 줄 데이터
     * @return 검증 완료(VERIFIED) 상태의 엔티티
     */
    public SmokingArea toEntity(SmokingAreaItem item) {
        Double lat = parseDouble(item.get(SmokingAreaItem.위도));
        Double lng = parseDouble(item.get(SmokingAreaItem.경도));

        Coordinate coordinate = new Coordinate(lat, lng);

        Point location = geometryFactory.createPoint(new org.locationtech.jts.geom.Coordinate(lng, lat));

        Address addressVo = Address.builder()
                .rawAddress(AddressUtil.refineForGeocoding(item.getAssembledAddress()))
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
                .location(location)
                .status(AreaStatus.VERIFIED)
                .refDate(item.get(SmokingAreaItem.데이터_기준_일자))
                .build();
    }

    /**
     * 사용자가 앱에서 제보한 DTO를 SmokingArea 엔티티로 변환
     * @param request 제보 요청 데이터
     * @return 승인 대기(PENDING) 상태의 엔티티
     */
    public SmokingArea toEntity(SmokingAreaSuggestRequest request) {
        // 1. 공간 데이터 생성
        Point location = geometryFactory.createPoint(
                new org.locationtech.jts.geom.Coordinate(request.longitude(), request.latitude())
        );

        // 2. VO 생성
        Coordinate coordinate = new Coordinate(request.latitude(), request.longitude());

        Address address = Address.builder()
                .rawAddress(request.address())
                .build();

        // 3. 엔티티 빌드
        return SmokingArea.builder()
                .areaNm(request.name())
                .areaDesc(request.description())
                .coordinate(coordinate)
                .address(address)
                .areaSe(request.type() != null ? request.type() : AreaType.GENERAL)
                .status(AreaStatus.PENDING) // 제보 상태 고정
                .fcltyKnd(request.imageUrl())
                .location(location)
                .refDate(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                .build();
    }

    /**
     * 문자열 숫자를 Double 타입으로 안전하게 변환
     * 숫자 외의 특수문자나 한글 단위를 제거한 후 파싱하여 런타임 에러 방지
     */
    private Double parseDouble(String val) {
        if (val == null || val.isBlank()) return 0.0;
        try {
            return Double.parseDouble(val.replaceAll("[^0-9.-]", ""));
        } catch (Exception e) {
            return 0.0;
        }
    }
}
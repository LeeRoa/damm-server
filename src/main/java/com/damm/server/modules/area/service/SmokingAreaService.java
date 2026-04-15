package com.damm.server.modules.area.service;

import com.damm.server.modules.area.dao.SmokingAreaDao;
import com.damm.server.modules.area.domain.SmokingArea;
import com.damm.server.modules.area.dto.*;
import com.damm.server.modules.area.mapper.SmokingAreaMapper;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SmokingAreaService {
    private final SmokingAreaDao smokingAreaDao;
    private final SmokingAreaMapper smokingAreaMapper;
    private final SmokingAreaWriter smokingAreaWriter;

    // PostGIS 공간 데이터를 만들기 위한 팩토리 (SRID 4326: WGS84 위경도 좌표계)
    private final GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);

    /**
     * 현 위치 기반 내 주변 흡연구역 조회
     */
    @Transactional(readOnly = true)
    public List<SmokingAreaSearchResponse> getNearbyAreas(NearbySmokingAreaRequest request) {
        return smokingAreaDao.getNearbyAreas(request);
    }

    /**
     * 검색어 기반 흡연구역 조회
     * @param request 검색어, 위치 정보, 정렬 기준 등을 담은 요청 객체
     * @return 조회된 흡연구역 리스트
     */
    @Transactional(readOnly = true)
    public List<SmokingAreaSearchResponse> searchAreas(SmokingAreaSearchRequest request) {
        return smokingAreaDao.searchAreas(request);
    }

    /**
     * 유저 신규 흡연구역 제보
     */
    @Transactional
    public Long suggestNewArea(SmokingAreaSuggestRequest request) {

        // 1. 매퍼를 통해 DTO -> Entity 기본 변환 (이때는 rawAddress만 있는 상태)
        SmokingArea newArea = smokingAreaMapper.toEntity(request);

        // 2. 바로 save하지 않고, Writer를 거쳐서 주소 보정 후 저장
        smokingAreaWriter.saveOrUpdate(newArea);

        return newArea.getInternalId();
    }

    /**
     * 지도 화면 영역(Bounding Box) 기반 핀 조회
     */
    public List<SmokingAreaPinResponse> getMapPins(BoundingBoxRequest request) {
        return smokingAreaDao.getAreasInBoundingBox(request);
    }
}

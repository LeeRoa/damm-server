package com.damm.server.modules.area.service;

import com.damm.server.modules.area.dao.SmokingAreaDao;
import com.damm.server.modules.area.domain.SmokingArea;
import com.damm.server.modules.area.dto.NearbySmokingAreaRequest;
import com.damm.server.modules.area.dto.SmokingAreaSearchRequest;
import com.damm.server.modules.area.dto.SmokingAreaSearchResponse;
import com.damm.server.modules.area.dto.SmokingAreaSuggestRequest;
import com.damm.server.modules.area.mapper.SmokingAreaMapper;
import com.damm.server.modules.area.repository.SmokingAreaRepository;
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
    private final SmokingAreaRepository smokingAreaRepository;
    private final SmokingAreaMapper smokingAreaMapper;

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
        SmokingArea suggestedArea = smokingAreaMapper.toEntity(request);
        return smokingAreaRepository.save(suggestedArea).getInternalId();
    }
}

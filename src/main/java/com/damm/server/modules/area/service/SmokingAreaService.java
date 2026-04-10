package com.damm.server.modules.area.service;

import com.damm.server.modules.area.dao.SmokingAreaDao;
import com.damm.server.modules.area.dto.NearbySmokingAreaRequest;
import com.damm.server.modules.area.dto.SmokingAreaSearchResponse;
import com.damm.server.modules.area.dto.SmokingAreaSearchRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SmokingAreaService {
    private final SmokingAreaDao smokingAreaDao;

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
}

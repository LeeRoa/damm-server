package com.damm.server.modules.area.service;

import com.damm.server.modules.area.repository.SmokingAreaRepository;
import com.damm.server.modules.area.dto.NearbySmokingAreaResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SmokingAreaService {
    private final SmokingAreaRepository smokingAreaRepository;

    /**
     * 현 위치 기반 근접 흡연구역 조회
     * @param lat 사용자 현재 위도
     * @param lng 사용자 현재 경도
     * @param radius 검색 반경 (m)
     */
    public List<NearbySmokingAreaResponse> getNearbySmokingAreas(double lat, double lng, double radius) {
        return smokingAreaRepository.findNearbyAreas(lat, lng, radius)
                .stream()
                .map(NearbySmokingAreaResponse::from)
                .toList();
    }
}

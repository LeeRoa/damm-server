package com.damm.server.modules.area.service;

import com.damm.server.infra.kakao.KakaoGeocodingClient;
import com.damm.server.modules.area.domain.SmokingArea;
import com.damm.server.modules.area.repository.SmokingAreaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class SmokingAreaWriter {

    private final SmokingAreaRepository smokingAreaRepository;
    private final KakaoGeocodingClient kakaoGeocodingClient;

    /**
     * 데이터를 저장하거나 업데이트한다.
     * 이때 좌표 정보가 없으면 카카오 API를 통해 보정한다.
     */
    @Transactional
    public void saveOrUpdate(SmokingArea newArea) {
        // 데이터 통일성을 위해 지오코딩을 수행한다.
        compensateCoordinate(newArea);

        // 기존 데이터 존재 여부에 따라 저장 또는 수정을 진행한다.
        smokingAreaRepository.findById(newArea.getId())
                .ifPresentOrElse(
                        existingArea -> existingArea.update(newArea),
                        () -> smokingAreaRepository.save(newArea)
                );
    }

    /**
     * 좌표 정보가 누락되었는지 확인한다.
     */
    private boolean isCoordinateMissing(SmokingArea area) {
        return area.getCoordinate() == null ||
                area.getCoordinate().getLatitude() == null ||
                area.getCoordinate().getLongitude() == null ||
                area.getCoordinate().getLatitude() == 0.0;
    }

    /**
     * 공공데이터 주소를 기반으로 위경도 좌표를 가져와 엔티티에 세팅한다.
     */
    private void compensateCoordinate(SmokingArea area) {
        String searchAddress = area.getAddress().getRawAddress();

        if (searchAddress.isBlank()) {
            area.updateGeocodingFail();
            return;
        }

        // 1. 확장된 데이터 가져오기
        var response = kakaoGeocodingClient.getGeocodingData(searchAddress);

        if (response != null) {
            area.updateGeocodingSuccess(response);
            log.debug("[표준화 완료] ID: {}, 도로명: {}", area.getId(), response.rdnmadr());
        } else {
            area.updateGeocodingFail();
            log.warn("[보정 실패] ID: {}, 검색어: {}", area.getId(), searchAddress);
        }
    }
}
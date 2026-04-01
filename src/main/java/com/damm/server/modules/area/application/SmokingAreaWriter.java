package com.damm.server.modules.area.application;

import com.damm.server.infra.kakao.KakaoGeocodingClient;
import com.damm.server.modules.area.domain.SmokingArea;
import com.damm.server.modules.area.domain.SmokingAreaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class SmokingAreaWriter {

    private final SmokingAreaRepository smokingAreaRepository;
    private final KakaoGeocodingClient kakaoGeocodingClient;

    /**
     * 데이터를 저장하거나 업데이트한다.
     * 이때 좌표 정보가 없으면 카카오 API를 통해 보정한다.
     */
    @Transactional
    public void saveOrUpdate(SmokingArea newArea) {
        // 1. 위도나 경도가 없는 경우 지오코딩을 수행한다.
        if (isCoordinateMissing(newArea)) {
            compensateCoordinate(newArea);
        }

        // 2. 기존 데이터 존재 여부에 따라 저장 또는 수정을 진행한다.
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
     * 도로명 주소를 기반으로 위경도 좌표를 가져와 엔티티에 세팅한다.
     */
    private void compensateCoordinate(SmokingArea area) {
        String address = area.getAddress().getRdnmadr();

        // 1. 확장된 데이터 가져오기
        var response = kakaoGeocodingClient.getGeocodingData(address);

        if (response != null) {
            area.compensateLocation(response);
            log.debug("[보정 완료] ID: {}, 지역: {}", area.getId(), response.emdnm());
        }
    }
}
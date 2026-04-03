package com.damm.server.modules.area.application;

import com.damm.server.infra.publicdata.PublicDataClient;
import com.damm.server.infra.publicdata.domain.ApiSource;
import com.damm.server.infra.publicdata.dto.SmokingAreaItem;
import com.damm.server.modules.area.domain.ApiSourceRepository;
import com.damm.server.modules.area.domain.SmokingArea;
import com.damm.server.modules.area.domain.SmokingAreaRepository;
import com.damm.server.modules.area.domain.enums.AddressStatus;
import com.damm.server.modules.area.domain.enums.AreaStatus;
import com.damm.server.modules.area.mapper.SmokingAreaMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class SmokingAreaBatchService {

    private final SmokingAreaRepository smokingAreaRepository;
    private final ApiSourceRepository apiSourceRepository;
    private final PublicDataClient publicDataClient;
    private final SmokingAreaWriter areaWriter;
    private final SmokingAreaMapper smokingAreaMapper;

    @Value("${public-data.api.initial-page:1}")
    private int initialPageNo;

    @Value("${public-data.api.page-size:1000}")
    private int numOfRows;

    /**
     * 정기적으로 실행되며 DB에 등록된 모든 활성 API 출처로부터 데이터를 수집합니다.
     */
    @Scheduled(cron = "${public-data.schedule.cron}")
    @Transactional
    public void syncAllApiSources() {
        log.info("모든 공공데이터 출처에 대한 동기화 작업을 시작합니다.");

        // 1. DB에서 활성화된 API 출처 리스트를 조회합니다.
        List<ApiSource> activeSources = apiSourceRepository.findAllByActiveTrue();

        if (activeSources.isEmpty()) {
            log.warn("활성화된 API 출처가 없어 작업을 종료합니다.");
            return;
        }

        for (ApiSource source : activeSources) {
            try {
                log.debug("[{}] 지역의 데이터를 수집합니다. (URL: {})", source.getFullRegionName(), source.getBaseUrl());
                syncSingleSource(source);
                source.updateSyncTime();
            } catch (Exception e) {
                log.error("[{}] 지역 동기화 중 오류가 발생했습니다: {}", source.getFullRegionName(), e.getMessage());
            }
        }

        log.info("모든 공공데이터 출처의 동기화 작업이 성공적으로 완료되었습니다.");
    }

    /**
     * 개별 API 출처에 대해 페이지별로 데이터를 긁어와 저장합니다.
     */
    private void syncSingleSource(ApiSource source) {
        int currentPage = initialPageNo;
        int totalPages = initialPageNo;

        while (currentPage <= totalPages) {
            var response = publicDataClient.fetchSmokingAreas(source, currentPage, numOfRows);

            if (response == null || response.items().isEmpty() || response.meta() == null) {
                log.warn("[{}] 데이터가 없거나 응답이 올바르지 않아 해당 지역 수집을 중단합니다. (페이지: {})",
                        source.getFullRegionName(), currentPage);
                break;
            }

            // 첫 번째 호출에서만 전체 건수를 확인하여 총 페이지 수를 계산합니다. (중복 호출 방지)
            if (currentPage == initialPageNo) {
                int totalCount = response.meta().totalCount();
                totalPages = (int) Math.ceil((double) totalCount / numOfRows);
                log.info("[{}] 전체 데이터 {}건, 총 {}페이지 분량의 동기화를 진행합니다.",
                        source.getFullRegionName(), totalCount, totalPages);
            }

            log.info("[{}] {}페이지의 데이터 {}건을 처리합니다.",
                    source.getFullRegionName(), currentPage, response.items().size());
            saveItems(response.items());

            currentPage++;
        }
    }

    /**
     * 추출된 데이터 리스트를 엔티티로 변환하여 DB에 반영합니다.
     */
    private void saveItems(List<SmokingAreaItem> items) {
        items.stream()
                .map(item -> {
                    try {
                        return smokingAreaMapper.toEntity(item);
                    } catch (Exception e) {
                        log.error("엔티티 변환 중 오류가 발생했습니다. (ID: {}): {}", item.get(SmokingAreaItem.KEY_ID), e.getMessage());
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .forEach(area -> {
                    try {
                        // findById로 기존 데이터를 체크하고 update 혹은 save를 수행합니다.
                        areaWriter.saveOrUpdate(area);
                    } catch (Exception e) {
                        log.error("데이터 저장 중 예외가 발생했습니다. (ID: {}): {}", area.getId(), e.getMessage());
                    }
                });
    }

    /**
     * PENDING 또는 FAIL 상태인 데이터를 찾아 지오코딩 재시도
     */
    @Scheduled(cron = "${public-data.retry.schedule.cron}")
    @Transactional
    public int processPendingAddresses() {
        // 1. 보정이 필요한 상태들 조회
        List<AddressStatus> targets = List.of(AddressStatus.PENDING, AddressStatus.FAIL);
        List<SmokingArea> pendingAreas = smokingAreaRepository.findByAddressStatusInAndStatusNot(targets, AreaStatus.CLOSED);

        int successCount = 0;
        log.info("[Batch] 주소 보정 시작 대상 건수: {}건", pendingAreas.size());

        for (SmokingArea area : pendingAreas) {
            try {
                areaWriter.saveOrUpdate(area);
                successCount++;
                Thread.sleep(100);
            } catch (Exception e) {
                log.error("[Batch] 보정 실패 - ID: {}, 사유: {}", area.getId(), e.getMessage());
            }
        }

        log.info("[Batch] 주소 보정 프로세스 완료");

        return successCount;
    }
}
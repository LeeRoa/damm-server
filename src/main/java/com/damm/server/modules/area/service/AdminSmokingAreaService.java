package com.damm.server.modules.area.service;

import com.damm.server.modules.area.domain.SmokingArea;
import com.damm.server.modules.area.domain.enums.AreaStatus;
import com.damm.server.modules.area.dto.AdminAreaApprovalRequest;
import com.damm.server.modules.area.repository.SmokingAreaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminSmokingAreaService {

    private final SmokingAreaRepository smokingAreaRepository;

    @Transactional(readOnly = true)
    public List<SmokingArea> getPendingAreas() {
        // PENDING 상태인 제보 목록만 가져오기
        // (실무에서는 페이징 처리가 필요하지만 일단 전체 조회로 시작)
        return smokingAreaRepository.findByStatus(AreaStatus.PENDING);
    }

    @Transactional
    public void processApproval(Long id, AdminAreaApprovalRequest request) {
        SmokingArea area = smokingAreaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 제보를 찾을 수 없습니다. ID: " + id));

        // 관리자가 수정한 데이터와 상태로 엔티티 업데이트 (트랜잭션 종료 시 자동 UPDATE 쿼리 발생)
        area.approveByAdmin(
                request.areaNm(),
                request.address(),
                request.areaSe(),
                request.status()
        );
    }
}
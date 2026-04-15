package com.damm.server.modules.area.service;

import com.damm.server.modules.area.domain.SmokingArea;
import com.damm.server.modules.area.domain.enums.AreaType;
import com.damm.server.modules.area.dto.SmokingAreaSuggestRequest;
import com.damm.server.modules.area.mapper.SmokingAreaMapper;
import com.damm.server.modules.area.repository.SmokingAreaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class SmokingAreaCommandServiceTest {

    @Mock
    private SmokingAreaRepository smokingAreaRepository;

    @Mock
    private SmokingAreaMapper smokingAreaMapper;

    @InjectMocks
    private SmokingAreaService smokingAreaCommandService;

    @Test
    @DisplayName("사용자가 새로운 흡연구역을 제보하면 성공적으로 저장되어야 한다")
    void suggestNewArea_Success() {
        // given
        SmokingAreaSuggestRequest request = new SmokingAreaSuggestRequest(
                "테스트 구역", "서울특별시 광진구", "건물 뒤편",
                37.5399, 127.0834, AreaType.GENERAL, "http://image.url"
        );

        // Mapper가 엔티티를 잘 반환한다고 가정 (더미 객체 생성)
        SmokingArea dummyEntity = SmokingArea.builder()
                .areaNm(request.name())
                .build();
        // internalId 수동 세팅 (Reflection 등으로 처리하거나 더미 리턴값 활용)

        given(smokingAreaMapper.toEntity(any(SmokingAreaSuggestRequest.class))).willReturn(dummyEntity);
        given(smokingAreaRepository.save(any(SmokingArea.class))).willReturn(dummyEntity);

        // when
        Long resultId = smokingAreaCommandService.suggestNewArea(request);

        // then
        verify(smokingAreaMapper).toEntity(request);
        verify(smokingAreaRepository).save(any(SmokingArea.class));
    }
}
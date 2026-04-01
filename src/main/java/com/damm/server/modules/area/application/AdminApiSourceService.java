package com.damm.server.modules.area.application;

import com.damm.server.infra.publicdata.domain.ApiSource;
import com.damm.server.modules.area.domain.ApiSourceRepository;
import com.damm.server.modules.area.dto.ApiSourceRegisterRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class AdminApiSourceService {

    private final ApiSourceRepository apiSourceRepository;

    public void register(ApiSourceRegisterRequest request) {
        ApiSource apiSource = ApiSource.builder()
                .regionName(request.regionName())
                .baseUrl(request.baseUrl())
                .parserType(request.parserType())
                .active(true)
                .build();

        apiSourceRepository.save(apiSource);
    }
}
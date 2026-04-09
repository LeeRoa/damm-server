package com.damm.server.modules.area.controller;

import com.damm.server.modules.area.dto.NearbySmokingAreaResponse;
import com.damm.server.modules.area.service.SmokingAreaService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/areas")
@RequiredArgsConstructor
public class SmokingAreaController {

    private final SmokingAreaService smokingAreaService;

    @GetMapping("/nearby")
    public List<NearbySmokingAreaResponse> getNearby(
            @RequestParam double lat,
            @RequestParam double lng,
            @RequestParam(defaultValue = "1000") double radius // 기본 반경 1km
    ) {
        return smokingAreaService.getNearbySmokingAreas(lat, lng, radius);
    }
}
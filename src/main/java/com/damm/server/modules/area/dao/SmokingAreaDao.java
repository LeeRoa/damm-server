package com.damm.server.modules.area.dao;

import com.damm.server.modules.area.dto.NearbySmokingAreaRequest;
import com.damm.server.modules.area.dto.SmokingAreaSearchResponse;
import com.damm.server.modules.area.dto.SmokingAreaSearchRequest;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SmokingAreaDao {
    List<SmokingAreaSearchResponse> getNearbyAreas(NearbySmokingAreaRequest request);
    List<SmokingAreaSearchResponse> searchAreas(SmokingAreaSearchRequest request);
}
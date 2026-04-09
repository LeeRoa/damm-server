package com.damm.server.modules.area.repository;

import com.damm.server.modules.area.domain.SmokingArea;
import com.damm.server.modules.area.domain.enums.AddressStatus;
import com.damm.server.modules.area.domain.enums.AreaStatus;
import com.damm.server.modules.area.dto.SmokingAreaDistanceProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface SmokingAreaRepository extends JpaRepository<SmokingArea, Long> {

    Optional<SmokingArea> findById(String id);

    List<SmokingArea> findByAddressStatusInAndStatusNot(Collection<AddressStatus> addressStatuses, AreaStatus status);

    /**
     * 반경 내 흡연구역을 거리순으로 조회 (PostGIS 네이티브 쿼리)
     * ST_MakePoint(경도, 위도) 주의!
     */
    @Query(nativeQuery = true, value = """
    SELECT
        internal_id AS internalId,
        id AS id,
        area_nm AS areaNm,
        area_desc AS areaDesc,
        area_se AS areaSe,
        raw_address AS rawAddress,
        latitude AS latitude,
        longitude AS longitude,
        fclty_knd AS fcltyKnd,
        status AS status,
        inst_nm AS instNm,
        ST_Distance(location, ST_MakePoint(:lng, :lat)::geography) AS distance
    FROM smoking_areas
    WHERE ST_DWithin(location, ST_MakePoint(:lng, :lat)::geography, :radius)
      AND status != 'CLOSED' -- 리스트에는 운영 중인 곳만 노출
    ORDER BY distance ASC
    """)
    List<SmokingAreaDistanceProjection> findNearbyAreas(
            @Param("lat") double lat,
            @Param("lng") double lng,
            @Param("radius") double radius
    );
}
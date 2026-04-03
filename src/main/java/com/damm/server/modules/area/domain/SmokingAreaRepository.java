package com.damm.server.modules.area.domain;

import com.damm.server.modules.area.domain.enums.AddressStatus;
import com.damm.server.modules.area.domain.enums.AreaStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface SmokingAreaRepository extends JpaRepository<SmokingArea, Long> {

    Optional<SmokingArea> findById(String id);

    List<SmokingArea> findByAddressStatusInAndStatusNot(Collection<AddressStatus> addressStatuses, AreaStatus status);
}
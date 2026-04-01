package com.damm.server.modules.area.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SmokingAreaRepository extends JpaRepository<SmokingArea, Long> {

    Optional<SmokingArea> findById(String id);

}
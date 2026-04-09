package com.damm.server.modules.area.repository;

import com.damm.server.infra.publicdata.domain.ApiSource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ApiSourceRepository extends JpaRepository<ApiSource, Long> {
    List<ApiSource> findAllByActiveTrue();
}

package com.sse.repository;

import com.sse.domain.Asset;
import com.sse.domain.AssetStatus;
import com.sse.domain.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface AssetRepository extends JpaRepository<Asset, Long> {
    @Transactional()
    List<Asset> findAll();

    @Transactional
    @Modifying
    @Query("update Asset a set a.status = :status, a.dateUpdated = :dateUpdated where a.id = :id")
    int updateStatus(@Param("id") Long id,
                     @Param("status") AssetStatus status,
                     @Param("dateUpdated") Instant dateUpdated);

    Optional<List<Asset>> findAssetsByLocationEquals(Location location);

    Optional<List<Asset>> findAssetsByLocation_Postcode(String postcode);
}

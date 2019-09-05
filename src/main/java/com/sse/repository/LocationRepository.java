package com.sse.repository;

import com.sse.domain.Location;
import com.sse.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface LocationRepository extends JpaRepository<Location, Long> {
    @Transactional()
    List<Location> findAllByPostcode(String postCode);

//    @Query(value = "SELECT l FROM Location l left join fetch l.assets where l.id = :id")
//    Location findLocationByIdWithAssets(@Param("id") Long id);
}

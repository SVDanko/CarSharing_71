package org.example.carsharing_71.repository;

import org.example.carsharing_71.domain.Location;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LocationRepository extends JpaRepository<Location, Long> {
}

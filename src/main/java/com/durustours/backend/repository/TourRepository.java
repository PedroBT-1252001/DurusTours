package com.durustours.backend.repository;

import com.durustours.backend.domain.Tour;
import com.durustours.backend.domain.TourCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TourRepository extends JpaRepository<Tour, Long> {

    List<Tour> findByActiveTrue();

    List<Tour> findByCategory(TourCategory category);
}

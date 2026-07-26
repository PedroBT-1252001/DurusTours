package com.durustours.backend.service;

import com.durustours.backend.dto.TourDTO;
import com.durustours.backend.exception.ResourceNotFoundException;
import com.durustours.backend.repository.TourRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TourService {

    private final TourRepository tourRepository;

    public List<TourDTO> getActiveTours() {
        return tourRepository.findByActiveTrue().stream()
                .map(TourDTO::from)
                .toList();
    }

    public TourDTO getTourById(Long id) {
        return tourRepository.findById(id)
                .map(TourDTO::from)
                .orElseThrow(() -> new ResourceNotFoundException("Tour not found: " + id));
    }
}

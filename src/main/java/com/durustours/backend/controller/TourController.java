package com.durustours.backend.controller;

import com.durustours.backend.dto.TourDTO;
import com.durustours.backend.service.TourService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/tours")
@RequiredArgsConstructor
public class TourController {

    private final TourService tourService;

    @GetMapping
    public List<TourDTO> getAllTours() {
        return tourService.getActiveTours();
    }

    @GetMapping("/{id}")
    public TourDTO getTourById(@PathVariable Long id) {
        return tourService.getTourById(id);
    }
}

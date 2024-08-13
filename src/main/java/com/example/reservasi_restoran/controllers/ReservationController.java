package com.example.reservasi_restoran.controllers;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.reservasi_restoran.dto.request.AddOrEditReservationDto;
import com.example.reservasi_restoran.dto.response.MessageResponse;
import com.example.reservasi_restoran.dto.response.WeeklyReservationResponseDto;
import com.example.reservasi_restoran.services.ReservationService;

@RestController
@RequestMapping("reservation")
public class ReservationController {
    @Autowired
    private ReservationService reservationService;

    @PostMapping("/add")
    public ResponseEntity<MessageResponse> addReservation(@RequestBody AddOrEditReservationDto addReservationDto) {
        return reservationService.addReservation(addReservationDto);
    }

    @GetMapping
    public ResponseEntity<WeeklyReservationResponseDto> getAllReservationsForWeek(
            @RequestParam("startDate") LocalDate startDate) {
        return reservationService.getAllReservationsForWeek(startDate);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MessageResponse> editReservation(
            @PathVariable Long id,
            @RequestBody AddOrEditReservationDto editReservationDto) {
        return reservationService.editReservation(id, editReservationDto);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<MessageResponse> deleteReservation(@PathVariable Long id) {
        return reservationService.deleteReservation(id);
    }
}

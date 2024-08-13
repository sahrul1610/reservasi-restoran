package com.example.reservasi_restoran.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.reservasi_restoran.models.Reservation;

public interface ReservationRepository extends JpaRepository<Reservation, Long>{
    List<Reservation> findByReservationDate(LocalDate reservationDate);

    List<Reservation> findByReservationDateBetween(LocalDate startDate, LocalDate endDate);

    Optional<Reservation> findById(Long id);
}

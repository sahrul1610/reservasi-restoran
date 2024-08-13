package com.example.reservasi_restoran.services;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.example.reservasi_restoran.dto.request.AddOrEditReservationDto;
import com.example.reservasi_restoran.dto.response.MessageResponse;
import com.example.reservasi_restoran.dto.response.ReservationDto;
import com.example.reservasi_restoran.dto.response.WeeklyReservationResponseDto;
import com.example.reservasi_restoran.models.Reservation;
import com.example.reservasi_restoran.repository.ReservationRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ReservationService {
    
    @Autowired 
    private ReservationRepository reservationRepository;

    private static final int MAX_RESERVATION_PER_DAY = 2;

    public ResponseEntity<WeeklyReservationResponseDto> getAllReservationsForWeek(LocalDate startDate) {
        LocalDate endDate = startDate.plusDays(6);
        List<Reservation> reservations = reservationRepository.findByReservationDateBetween(startDate, endDate);

        List<ReservationDto> reservationDtos = reservations.stream()
            .map(reservation -> ReservationDto.builder()
                .reservationId(reservation.getReservationId())
                .customerName(reservation.getCustomerName())
                .reservationDate(reservation.getReservationDate())
                .build())
            .collect(Collectors.toList());

        WeeklyReservationResponseDto responseDto = WeeklyReservationResponseDto.builder()
            .startDate(startDate)
            .endDate(endDate)
            .reservations(reservationDtos)
            .build();

        return ResponseEntity.ok(responseDto);
    }

    public ResponseEntity<MessageResponse> addReservation(AddOrEditReservationDto addReservationDto) {
        try {
            LocalDate reservationDate = addReservationDto.getReservationDate();

            if (!isAvailable(reservationDate)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                    new MessageResponse(HttpStatus.FORBIDDEN.value(), "Reservasi tidak dapat dilakukan pada hari libur", null, null));
            }

            long count = reservationRepository.findByReservationDate(reservationDate).size();
            if (count >= MAX_RESERVATION_PER_DAY) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                    new MessageResponse(HttpStatus.FORBIDDEN.value(), "Jumlah reservasi untuk tanggal ini sudah mencapai batas", null, null));
            }

            Reservation reservation = new Reservation();
            reservation.setCustomerName(addReservationDto.getCustomerName());
            reservation.setReservationDate(reservationDate);
            reservationRepository.save(reservation);
    
            MessageResponse response = MessageResponse.builder()
                .statusCode(HttpStatus.CREATED.value())
                .message("Reservasi berhasil ditambahkan")
                .customerName(reservation.getCustomerName())
                .reservationDate(reservation.getReservationDate())
                .build();
    
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            log.error("Gagal menambahkan reservasi", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                new MessageResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Gagal menambahkan reservasi", null, null));
        }
    }

    public ResponseEntity<MessageResponse> editReservation(Long id, AddOrEditReservationDto editReservationDto) {
        try {
            Optional<Reservation> optionalReservation = reservationRepository.findById(id);

            if (optionalReservation.isPresent()) {
                Reservation reservation = optionalReservation.get();
                LocalDate newDate = editReservationDto.getReservationDate();
                
                if (!isAvailable(newDate)) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                        new MessageResponse(HttpStatus.FORBIDDEN.value(), "Reservasi tidak dapat dilakukan pada hari libur", null, null));
                }

                long count = reservationRepository.findByReservationDate(newDate).size();
                if (count >= MAX_RESERVATION_PER_DAY) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                        new MessageResponse(HttpStatus.FORBIDDEN.value(), "Jumlah reservasi untuk tanggal ini sudah mencapai batas", null, null));
                }

                reservation.setCustomerName(editReservationDto.getCustomerName());
                reservation.setReservationDate(newDate);
                reservationRepository.save(reservation);

                return ResponseEntity.ok(
                    new MessageResponse(HttpStatus.OK.value(), "Reservasi berhasil diperbarui", reservation.getCustomerName(), reservation.getReservationDate()));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new MessageResponse(HttpStatus.NOT_FOUND.value(), "Reservasi tidak ditemukan", null, null));
            }
        } catch (Exception e) {
            log.error("Gagal memperbarui reservasi", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                new MessageResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Gagal memperbarui reservasi", null, null));
        }
    }

    public ResponseEntity<MessageResponse> deleteReservation(Long id) {
        try {
            Optional<Reservation> optionalReservation = reservationRepository.findById(id);

            if (optionalReservation.isPresent()) {
                reservationRepository.deleteById(id);
                return ResponseEntity.ok(
                    new MessageResponse(HttpStatus.OK.value(), "Reservasi berhasil dihapus", null, null));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new MessageResponse(HttpStatus.NOT_FOUND.value(), "Reservasi tidak ditemukan", null, null));
            }
        } catch (Exception e) {
            log.error("Gagal menghapus reservasi", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                new MessageResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Gagal menghapus reservasi", null, null));
        }
    }

    public boolean isAvailable(LocalDate date) {
        if (date.getDayOfWeek() == DayOfWeek.WEDNESDAY || date.getDayOfWeek() == DayOfWeek.FRIDAY) {
            return false;
        }

        return true;
    }
}
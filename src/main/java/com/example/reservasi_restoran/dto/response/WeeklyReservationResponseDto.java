package com.example.reservasi_restoran.dto.response;

import java.time.LocalDate;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WeeklyReservationResponseDto {
    private LocalDate startDate;
    private LocalDate endDate;
    private List<ReservationDto> reservations;
}

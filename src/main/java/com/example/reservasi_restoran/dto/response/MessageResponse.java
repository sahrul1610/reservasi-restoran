package com.example.reservasi_restoran.dto.response;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MessageResponse {
    private int statusCode;
    private String message;
    private String customerName;
    private LocalDate reservationDate;
}

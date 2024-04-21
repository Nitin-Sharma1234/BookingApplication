package com.hotel.booking.dtos.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class RoomDTO {
    @NotBlank(message = "Type is required")
    private String type;

    @NotBlank(message = "Description is required")
    private String description;

    @Positive(message = "Max occupancy must be positive")
    private int maxOccupancy;

    @Positive(message = "Price per night must be positive")
    private double pricePerNight;
}
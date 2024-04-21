package com.hotel.booking.dtos.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class CreateRoom {

    @NotNull(message = "Hotel ID is required")
    private Long hotelId;

    @NotBlank(message = "Type is required")
    private String type;

    @NotBlank(message = "Description is required")
    private String description;

    @Positive(message = "Price per night must be positive")
    private Double pricePerNight;

    @Positive(message = "Max occupancy must be positive")
    private Integer maxOccupancy;

}

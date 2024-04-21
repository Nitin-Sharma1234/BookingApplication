package com.hotel.booking.dtos.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class CreateHotel {
    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Location is required")
    private String location;

    @NotBlank(message = "Description is required")
    private String description;

    @NotNull(message = "Rating is required")
    private Double rating;

    @NotEmpty(message = "Amenities list cannot be empty")
    @Size(min = 1, message = "At least one amenity must be provided")
    private List<@NotBlank(message = "Amenity cannot be blank") String> amenities;

    @NotNull
    @Size(min = 1)
    private List<RoomDTO> rooms;

}

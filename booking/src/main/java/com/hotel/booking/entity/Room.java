
package com.hotel.booking.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "room",
        indexes = {@Index(columnList = "type,price_per_night ,hotel_id")})
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "hotel_id")
    private Hotel hotel;
    private String type;
    private String description;
    private double pricePerNight;
    private int maxOccupancy;

}

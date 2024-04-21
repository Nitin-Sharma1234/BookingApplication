package com.hotel.booking.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Data
@Table(name = "hotel",
        indexes = {@Index(columnList = "name, location")})
public class Hotel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String location;
    private String description;
    private double rating;
    private List<String> amenities;

    public Hotel(Long id) {
        this.id = id;
    }

    public Hotel() {
    }
}

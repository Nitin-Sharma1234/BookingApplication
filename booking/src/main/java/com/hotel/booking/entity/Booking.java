package com.hotel.booking.entity;

import com.hotel.booking.enums.BookingStatus;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "booking",
        indexes = {@Index(columnList = "room_id, user_id, check_in_date, check_out_date, status")})
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "room_id")
    private Room room;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private long checkInDate;
    private long checkOutDate;
    private double totalPrice;

    @Enumerated(EnumType.STRING)
    private BookingStatus status;

    public boolean overlaps(long startDateMillis, long endDateMillis) {
        // Check if the booking overlaps with the given date range
        return !(this.checkOutDate < startDateMillis || this.checkInDate > endDateMillis);
    }
}

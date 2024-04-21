package com.hotel.booking.service;

import com.hotel.booking.constants.ApplicationConstants;
import com.hotel.booking.entity.Booking;
import com.hotel.booking.enums.BookingStatus;
import com.hotel.booking.repository.BookingRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class SchedulerService {

    @Autowired
    private BookingRepo bookingRepo;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;


    /**
     * This Java function updates DB the status of expired check-out bookings to "VACANT" and deletes
     * corresponding keys from a Redis cache.
     */
    @Scheduled(fixedRateString ="${expire.room.checked.timeout}" )
    public void updateCheckOutBooking() {
        List<Booking> bookings = bookingRepo.findAllCheckOutExpire(new Date().getTime());
        List<String> redisKeys = new ArrayList<>();
        bookings.forEach(element -> {
            element.setStatus(BookingStatus.VACANT);
            redisKeys.add(ApplicationConstants.ROOM_AVAILABILITY + element.getRoom().getId());
        });
        bookingRepo.saveAll(bookings);
        redisTemplate.delete(redisKeys);
    }

}

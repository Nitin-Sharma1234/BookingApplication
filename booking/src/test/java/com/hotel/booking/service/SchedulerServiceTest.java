package com.hotel.booking.service;

import com.hotel.booking.entity.Booking;
import com.hotel.booking.repository.BookingRepo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SchedulerServiceTest {

    @Mock
    private BookingRepo bookingRepo;

    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @InjectMocks
    private SchedulerService schedulerService;

    @Test
    public void testUpdateCheckOutBooking() {
        List<Booking> bookings = new ArrayList<>();
        List<String> redisKeys = new ArrayList<>();
        when(bookingRepo.findAllCheckOutExpire(anyLong())).thenReturn(bookings);

        schedulerService.updateCheckOutBooking();
        verify(bookingRepo).findAllCheckOutExpire(anyLong());
        verify(bookingRepo).saveAll(bookings);
        verify(redisTemplate).delete(redisKeys);
    }
}


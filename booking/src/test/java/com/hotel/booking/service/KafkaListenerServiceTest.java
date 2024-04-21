package com.hotel.booking.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hotel.booking.constants.ApplicationConstants;
import com.hotel.booking.entity.Booking;
import com.hotel.booking.entity.Room;
import com.hotel.booking.repository.BookingRepo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class KafkaListenerServiceTest {

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private BookingRepo bookingRepo;

    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @InjectMocks
    private kafkaListenerService kafkaListenerService;

    @Test
    public void testBookingConsumer() throws JsonProcessingException {
        String bookingMessage = "{\"id\":\"123\",\"name\":\"John Doe\"}";
        Booking booking = new Booking();
        booking.setId(1L);
        when(objectMapper.readValue(bookingMessage, Booking.class)).thenReturn(booking);
        kafkaListenerService.bookingConsumer(bookingMessage);
        verify(objectMapper).readValue(bookingMessage, Booking.class);
        verify(bookingRepo).save(booking);
    }

    @Test
    public void testDltHandler() throws JsonProcessingException {
        String bookingMessage = "{\"id\":\"123\",\"name\":\"John Doe\"}";

        Booking booking = new Booking();
        booking.setId(1L);
        Room room = new Room();
        room.setId(1L);
        booking.setRoom(room);

        when(objectMapper.readValue(bookingMessage, Booking.class)).thenReturn(booking);
        kafkaListenerService.dltHandler(bookingMessage);
        verify(objectMapper).readValue(bookingMessage, Booking.class);
        String redisKey = ApplicationConstants.ROOM_AVAILABILITY + booking.getRoom().getId();
        verify(redisTemplate).delete(redisKey);

    }
}

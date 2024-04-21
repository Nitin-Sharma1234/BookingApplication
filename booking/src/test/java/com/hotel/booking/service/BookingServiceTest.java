package com.hotel.booking.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hotel.booking.config.MessageProducer;
import com.hotel.booking.dtos.request.CreateBooking;
import com.hotel.booking.entity.Booking;
import com.hotel.booking.entity.Hotel;
import com.hotel.booking.entity.Room;
import com.hotel.booking.entity.User;
import com.hotel.booking.enums.BookingStatus;
import com.hotel.booking.repository.BookingRepo;
import com.hotel.booking.repository.RoomRepo;
import com.hotel.booking.repository.UserRepo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BookingServiceTest {

    @Mock
    private BookingRepo mockBookingRepo;
    @Mock
    private UserRepo mockUserRepo;
    @Mock
    private RoomRepo mockRoomRepo;
    @Mock
    private MessageProducer mockMessageProducer;
    @Mock
    private RedisTemplate<String, String> mockRedisTemplate;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private HashOperations hashOperations;

    @InjectMocks
    private BookingService bookingServiceUnderTest;

    long checkinTime = new Date().getTime();

    long checkOutTime = checkinTime + (24 * 60 * 60 * 1000);


    @Test
     void testBookHotel() {
        final CreateBooking createBooking = new CreateBooking();
        createBooking.setRoomId(1L);
        createBooking.setUserId(0L);
        createBooking.setCheckInDate(checkinTime);
        createBooking.setCheckOutDate(checkOutTime);
        createBooking.setTotalPrice(0.0);

        final Booking expectedResult = new Booking();
        expectedResult.setId(0L);
        final Room room = new Room();
        room.setPricePerNight(0.0);
        expectedResult.setRoom(room);
        final User user = new User();
        expectedResult.setUser(user);
        expectedResult.setStatus(BookingStatus.BOOKED);
        final User user2 = new User();
        user2.setId(0L);
        user2.setUsername("username");
        user2.setEmail("email");
        user2.setPassword("password");
        user2.setFirstName("firstName");
        final Optional<User> user1 = Optional.of(user2);
        when(mockUserRepo.findById(any())).thenReturn(user1);

        final Room room2 = new Room();
        room2.setId(0L);
        final Hotel hotel = new Hotel();
        hotel.setId(0L);
        hotel.setName("name");
        room2.setHotel(hotel);
        room2.setPricePerNight(0.0);
        final Optional<Room> room1 = Optional.of(room2);
        when(mockRoomRepo.findById(1L)).thenReturn(room1);
        when(mockRedisTemplate.opsForHash()).thenReturn(hashOperations);

        when(mockBookingRepo.findByRoomIdAndDateRange(any(), anyLong(), anyLong())).thenReturn(Collections.emptyList());
        when(objectMapper.convertValue(createBooking, Booking.class)).thenReturn(expectedResult);

        final Booking result = bookingServiceUnderTest.bookHotel(createBooking);
        assertEquals(expectedResult, result);
    }

    @Test
    public void testIsRoomAvailable() {
        when(mockRedisTemplate.opsForHash()).thenReturn(hashOperations);
        final boolean result = bookingServiceUnderTest.isRoomAvailable(1L, checkinTime, checkOutTime);
        assertTrue(result);
    }

    @Test
    public void testCalculateTotalPrice() {
        assertEquals(0.0, bookingServiceUnderTest.calculateTotalPrice(checkinTime, checkOutTime, 0.0), 0.0001);
    }

    @Test
    public void testGetBookings() {
        final Booking booking = new Booking();
        booking.setId(0L);
        final Room room = new Room();
        room.setPricePerNight(0.0);
        booking.setRoom(room);
        final User user = new User();
        booking.setUser(user);
        booking.setStatus(BookingStatus.BOOKED);
        final Page<Booking> bookings = new PageImpl<>(List.of(booking));
        when(mockBookingRepo.findAll(any(Pageable.class))).thenReturn(bookings);

        final Page<Booking> result = bookingServiceUnderTest.getBookings(0, 10);
        Assertions.assertNotNull(result);
    }


    @Test
    public void testCancelBooking() {
        final Booking booking1 = new Booking();
        booking1.setId(0L);
        final Room room = new Room();
        room.setPricePerNight(0.0);
        booking1.setRoom(room);
        final User user = new User();
        booking1.setUser(user);
        booking1.setStatus(BookingStatus.BOOKED);
        final Optional<Booking> booking = Optional.of(booking1);
        when(mockBookingRepo.findById(0L)).thenReturn(booking);

        final String result = bookingServiceUnderTest.cancelBooking(0L);
        assertEquals("Booking cancel successful", result);
    }

    @Test
    public void testGetBookingsByUser() {
        final Booking booking = new Booking();
        booking.setId(0L);
        final Room room = new Room();
        room.setPricePerNight(0.0);
        booking.setRoom(room);
        final User user = new User();
        booking.setUser(user);
        booking.setStatus(BookingStatus.BOOKED);
        final List<Booking> expectedResult = List.of(booking);

        final Booking booking1 = new Booking();
        booking1.setId(0L);
        final Room room1 = new Room();
        room1.setPricePerNight(0.0);
        booking1.setRoom(room1);
        final User user1 = new User();
        booking1.setUser(user1);
        booking1.setStatus(BookingStatus.BOOKED);
        final List<Booking> bookings = List.of(booking1);
        when(mockBookingRepo.getBookingsByUser(eq(0), any(Pageable.class))).thenReturn(bookings);

        final List<Booking> result = bookingServiceUnderTest.getBookingsByUser(0, 10);
        assertEquals(expectedResult, result);
    }


}

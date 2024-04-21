package com.hotel.booking.service;

import static org.mockito.Mockito.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hotel.booking.dtos.request.RoomDTO;
import com.hotel.booking.entity.Hotel;
import com.hotel.booking.entity.Room;
import com.hotel.booking.repository.HotelRepo;
import com.hotel.booking.repository.RoomRepo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import java.util.Collections;
import java.util.NoSuchElementException;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class RoomServiceTest {

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private RoomRepo roomRepo;

    @Mock
    private HotelRepo hotelRepo;

    @InjectMocks
    private RoomService roomService;

    @Test
    public void testCreateRoom() {
        long hotelId = 1L;
        RoomDTO roomDTO = new RoomDTO();
        Hotel mockHotel = new Hotel();
        when(hotelRepo.findById(hotelId)).thenReturn(Optional.of(mockHotel));
        Room mockRoom = new Room();
        when(objectMapper.convertValue(roomDTO, Room.class)).thenReturn(mockRoom);
        when(roomRepo.save(mockRoom)).thenReturn(mockRoom);
        Room createdRoom = roomService.createRoom(hotelId, roomDTO);
        verify(hotelRepo).findById(hotelId);
        verify(objectMapper).convertValue(roomDTO, Room.class);
        verify(roomRepo).save(mockRoom);
        assertEquals(mockRoom, createdRoom);
    }

    @Test
    public void testCreateRoom_HotelNotFound() {
        long hotelId = 1L;
        RoomDTO roomDTO = new RoomDTO();
        when(hotelRepo.findById(hotelId)).thenReturn(Optional.empty());
        assertThrows(NoSuchElementException.class, () -> {
            roomService.createRoom(hotelId, roomDTO);
        });
        verify(hotelRepo).findById(hotelId);
        verifyNoMoreInteractions(objectMapper, roomRepo);
    }

    @Test
    public void testGetRooms() {
        Page<Room> mockPage = new PageImpl<>(Collections.emptyList());
        when(roomRepo.findAll(any(Pageable.class))).thenReturn(mockPage);
        Page<Room> resultPage = roomService.getRooms(0, 10);
        verify(roomRepo).findAll(Pageable.ofSize(10));
        assertNotNull(resultPage);
    }
}

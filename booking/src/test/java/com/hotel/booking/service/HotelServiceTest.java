package com.hotel.booking.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hotel.booking.dtos.request.CreateHotel;
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

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class HotelServiceTest {

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private HotelRepo hotelRepo;

    @Mock
    private RoomRepo roomRepo;

    @InjectMocks
    private HotelService hotelService;


    @Test
    public void testCreateHotel() {
        CreateHotel createHotel = new CreateHotel();
        createHotel.setName("Test Hotel");

        List<RoomDTO> roomDTOList = new ArrayList<>();
        roomDTOList.add(new RoomDTO());
        createHotel.setRooms(roomDTOList);
        when(objectMapper.convertValue(createHotel, Hotel.class)).thenReturn(new Hotel(1L));
        when(objectMapper.convertValue(new RoomDTO(), Room.class)).thenReturn(new Room());
        when(hotelRepo.save(any(Hotel.class))).thenReturn(new Hotel(1L));

        hotelService.createHotel(createHotel);

        verify(objectMapper).convertValue(createHotel, Hotel.class);
        verify(hotelRepo).save(any(Hotel.class));
        verify(roomRepo).saveAll(anyList());
    }

    @Test
    public void testMapRoomDTOListToEntityList() {
        List<RoomDTO> roomDTOList = new ArrayList<>();
        RoomDTO roomDTO = new RoomDTO();
        roomDTOList.add(roomDTO);
        Room expectedRoom = new Room();

        when(objectMapper.convertValue(roomDTO, Room.class)).thenReturn(expectedRoom);
        List<Room> rooms = hotelService.mapRoomDTOListToEntityList(roomDTOList, 1L);

        assertNotNull(rooms);
        assertEquals(roomDTOList.size(), rooms.size());
    }

    @Test
    public void testGetHotels() {
        Page<Hotel> page = new PageImpl<>(new ArrayList<>());
        when(hotelRepo.findAll(any(Pageable.class))).thenReturn(page);

        Page<Hotel> resultPage = hotelService.getHotels(0, 10);

        verify(hotelRepo).findAll(Pageable.ofSize(10));
        assertNotNull(resultPage);
    }
}

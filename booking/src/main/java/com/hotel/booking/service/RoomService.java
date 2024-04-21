package com.hotel.booking.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hotel.booking.dtos.request.RoomDTO;
import com.hotel.booking.entity.Hotel;
import com.hotel.booking.entity.Room;
import com.hotel.booking.exception.CustomException;
import com.hotel.booking.repository.HotelRepo;
import com.hotel.booking.repository.RoomRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
public class RoomService {
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RoomRepo roomRepo;

    @Autowired
    private HotelRepo hotelRepo;

    /**
     * The function creates a room associated with a specific hotel using the provided RoomDTO object.
     *
     * @param hotelId The `hotelId` parameter is the unique identifier of the hotel to which the room
     *                belongs. It is used to retrieve the hotel entity from the database using the
     *                `hotelRepo.findById(hotelId)` method.
     * @param roomDTO The `roomDTO` parameter is an object of type `RoomDTO` which contains the details
     *                of the room that needs to be created. It likely includes information such as the room number, room
     *                type, price, and any other relevant details about the room.
     * @return The `createRoom` method returns a `Room` object.
     */
    public Room createRoom(long hotelId, RoomDTO roomDTO) {
        Hotel hotel = hotelRepo.findById(hotelId)
                .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND.value(), "Hotel not found with Id : " + hotelId));
        Room room = objectMapper.convertValue(roomDTO, Room.class);
        room.setHotel(hotel);
        return roomRepo.save(room);
    }

    /**
     * The function `getRooms` retrieves a page of Room entities from the database based on the specified
     * page number and size.
     *
     * @param page The `page` parameter in the `getRooms` method represents the page number of results to
     *             retrieve. It is used to specify which page of results to return when paginating through a
     *             collection of data.
     * @param size The `size` parameter in the `getRooms` method represents the number of items (rooms)
     *             to be displayed on each page of the paginated results. It determines the size of the page when
     *             fetching rooms from the database.
     * @return The method is returning a Page of Room objects from the roomRepo repository, with the
     * specified size for pagination.
     */
    public Page<Room> getRooms(int page, int size) {
        return roomRepo.findAll(Pageable.ofSize(size));
    }

}

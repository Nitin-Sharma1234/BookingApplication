package com.hotel.booking.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hotel.booking.dtos.request.CreateHotel;
import com.hotel.booking.dtos.request.RoomDTO;
import com.hotel.booking.entity.Hotel;
import com.hotel.booking.entity.Room;
import com.hotel.booking.repository.HotelRepo;
import com.hotel.booking.repository.RoomRepo;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HotelService {
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private HotelRepo hotelRepo;

    @Autowired
    private RoomRepo roomRepo;

   /**
    * The `createHotel` method creates a new hotel entity, saves it to the database, maps and saves
    * room entities associated with the hotel, and returns the created hotel entity.
    * 
    * @param createHotel The `createHotel` parameter seems to be an object of type `CreateHotel` which
    * is used to create a new `Hotel` entity. It is likely a DTO (Data Transfer Object) that contains
    * information needed to create a new hotel, such as the hotel's name, address, and other
    * @return The `createHotel` method is returning an instance of the `Hotel` entity that was created
    * and saved in the database.
    */
    @Transactional
    public Hotel createHotel(CreateHotel createHotel) {
        Hotel hotel = objectMapper.convertValue(createHotel, Hotel.class);
        hotel = hotelRepo.save(hotel);
        List<Room> rooms = mapRoomDTOListToEntityList(createHotel.getRooms(), hotel.getId());
        roomRepo.saveAll(rooms);
        return hotel;
    }

    /**
     * This Java function maps a list of RoomDTO objects to a list of Room entities with a specified
     * hotel ID.
     *
     * @param roomDTOList The `roomDTOList` parameter is a list of `RoomDTO` objects, which likely
     *                    contain information about rooms in a hotel such as room number, type, price, etc.
     * @param hotelId     The `hotelId` parameter is a `Long` value that represents the unique identifier of
     *                    a hotel to which the rooms belong.
     * @return A List of Room entities is being returned.
     */
    public List<Room> mapRoomDTOListToEntityList(List<RoomDTO> roomDTOList, Long hotelId) {
        return roomDTOList.stream()
                .map(roomDTO -> mapRoomDTOToEntity(roomDTO, hotelId))
                .toList();
    }

    /**
     * The function `mapRoomDTOToEntity` converts a RoomDTO object to a Room entity, sets the hotel ID,
     * and returns the Room entity.
     *
     * @param roomDTO The `roomDTO` parameter is an object of type `RoomDTO`, which likely contains
     *                information about a room such as its number, type, price, and any other relevant details.
     * @param hotelId The `hotelId` parameter is the identifier of the hotel to which the room belongs.
     *                It is used to set the hotel for the room entity being created or updated in the
     *                `mapRoomDTOToEntity` method.
     * @return The method `mapRoomDTOToEntity` returns a `Room` entity object.
     */
    private Room mapRoomDTOToEntity(RoomDTO roomDTO, Long hotelId) {
        Room room = objectMapper.convertValue(roomDTO, Room.class);
        room.setHotel(new Hotel(hotelId));
        return room;
    }

    /**
     * The function returns a page of Hotel entities from the hotel repository based on the specified
     * page number and size.
     *
     * @param page The `page` parameter in the `getHotels` method represents the page number of the
     *             results you want to retrieve. It is used to specify which page of results you want to fetch from
     *             the database.
     * @param size The `size` parameter in the `getHotels` method represents the number of items (in
     *             this case, hotels) to be included in each page of results when paginating through the list of
     *             hotels. It determines how many hotels will be displayed on each page when the method is called.
     * @return A Page of Hotel entities from the hotelRepo, with the specified size.
     */
    public Page<Hotel> getHotels(int page, int size) {
        return hotelRepo.findAll(Pageable.ofSize(size));
    }

}

package com.hotel.booking.controller;

import com.hotel.booking.dtos.request.RoomDTO;
import com.hotel.booking.service.RoomService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Tag(name = "Rooms")
@RequestMapping("/room")
public class RoomController {
    @Autowired
    private RoomService roomService;

    /**
     * This function is used for adding a room to a hotel.
     *
     * @param hotelId The `hotelId` parameter is a path variable representing the unique identifier of
     *                the hotel to which the room will be added.
     * @param roomDTO The `roomDTO` parameter in the `createRoom` method is of type `RoomDTO` and is
     *                annotated with `@Valid`. This annotation is used to indicate that the input should be validated
     *                based on validation constraints defined in the `RoomDTO` class or any validation annotations
     *                present on its fields
     * @return The method `createRoom` is returning a `ResponseEntity` with status code `201 Created`
     * and the body containing the result of the `roomService.createRoom` method call.
     */
    @PostMapping("/hotel/{hotelId}")
    @Operation(summary = "This API is used for adding room.", description = "Adding Room")
    public ResponseEntity<?> createRoom(@PathVariable long hotelId, @RequestBody @Valid RoomDTO roomDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(roomService.createRoom(hotelId, roomDTO));
    }

    /**
     * This Java API endpoint retrieves all rooms based on the specified pagination parameters.
     *
     * @param page The `page` parameter in the `getRooms` method is used to specify the page number of
     *             the results to be retrieved. It is typically used for pagination, where large sets of data are
     *             divided into pages to improve performance and user experience.
     * @param size The `size` parameter in the `getRooms` method is used to specify the number of items
     *             (in this case, rooms) to be returned per page when paginating the results. It determines the size
     *             of each page of results that will be returned in the response.
     * @return The `getRooms` method is returning a `ResponseEntity` object with the result of calling
     * the `roomService.getRooms(page, size)` method.
     */
    @GetMapping
    @Operation(summary = "This API is used for getting all Rooms.", description = "Get All Rooms")
    public ResponseEntity<?> getRooms(@RequestParam int page, @RequestParam int size) {
        return ResponseEntity.ok(roomService.getRooms(page, size));
    }
}

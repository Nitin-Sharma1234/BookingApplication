package com.hotel.booking.controller;

import com.hotel.booking.dtos.request.CreateHotel;
import com.hotel.booking.entity.Hotel;
import com.hotel.booking.service.HotelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Tag(name = "Hotels")
@RequestMapping("/hotel")
public class HotelController {
    @Autowired
    private HotelService hotelService;

    /**
     * This Java function creates a hotel registration API endpoint.
     *
     * @param createHotel The `createHotel` parameter in the `createHotel` method is of type
     *                    `CreateHotel`, which is a class representing the data needed to create a new hotel. This
     *                    parameter is annotated with `@RequestBody` to indicate that the data will be obtained from the
     *                    request body, and `@Valid
     * @return A `ResponseEntity` object containing a `Hotel` object is being returned. The HTTP status
     * code is set to `201 Created`, and the body of the response contains the hotel created by the
     * `hotelService.createHotel(createHotel)` method.
     */
    @PostMapping
    @Operation(summary = "This API is used for register hotel.", description = "Register Hotel")
    public ResponseEntity<Hotel> createHotel(@RequestBody @Valid CreateHotel createHotel) {
        return ResponseEntity.status(HttpStatus.CREATED).body(hotelService.createHotel(createHotel));
    }

    /**
     * This Java API endpoint retrieves a page of hotels based on the specified page number and size.
     *
     * @param page The `page` parameter in the `getHotel` method is used to specify the page number of
     *             the results to retrieve. It is typically used for pagination, where large sets of data are
     *             divided into pages to improve performance and user experience.
     * @param size The `size` parameter in the `getHotel` method is used to specify the number of items
     *             (hotels) to be displayed per page when fetching the hotels. It determines the size of the page in
     *             the paginated response.
     * @return A `ResponseEntity` containing a `Page` of `Hotel` objects is being returned.
     */
    @GetMapping
    @Operation(summary = "This API is used for getting all Hotels.", description = "Get All Hotels")
    public ResponseEntity<Page<Hotel>> getHotel(@RequestParam int page, @RequestParam int size) {
        return ResponseEntity.ok(hotelService.getHotels(page, size));
    }
}

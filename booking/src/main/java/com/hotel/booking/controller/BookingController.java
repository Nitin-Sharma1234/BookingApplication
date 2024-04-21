package com.hotel.booking.controller;


import com.hotel.booking.dtos.request.CreateBooking;
import com.hotel.booking.service.BookingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


@Tag(name = "Bookings")
@RestController
@RequestMapping("/bookings")
public class BookingController {
    @Autowired
    private BookingService bookingService;

    /**
     * This function is used to book a hotel by creating a booking with the provided details.
     *
     * @param createBooking The `createBooking` parameter in the `bookHotel` method is of type
     *                      `CreateBooking` and is annotated with `@RequestBody` and `@Valid`. This means that the method
     *                      expects a request body in the form of a JSON object that can be mapped to the `CreateBooking`
     *                      class
     * @return The method `bookHotel` is returning a `ResponseEntity` object with a status of
     * `HttpStatus.CREATED` and the body containing the result of the
     * `bookingService.bookHotel(createBooking)` method call.
     */
    @PostMapping
    @Operation(summary = "This API is used for book  hotel.", description = "Book Hotel")
    public ResponseEntity<?> bookHotel(@RequestBody @Valid CreateBooking createBooking) {
        return ResponseEntity.status(HttpStatus.CREATED).body(bookingService.bookHotel(createBooking));
    }

    /**
     * This API endpoint retrieves all bookings with pagination support.
     *
     * @param page The `page` parameter in the `getBookings` method is used to specify the page number
     *             of the results to be retrieved. It is typically used for pagination, where large sets of data
     *             are divided into pages to improve performance and user experience. The `page` parameter
     *             indicates which page of results
     * @param size The `size` parameter in the `getBookings` method is used to specify the number of
     *             items (bookings) to be returned per page when paginating the results. It determines the size of
     *             each page in the paginated response.
     * @return The `getBookings` method is returning a `ResponseEntity` object with the result of
     * calling the `bookingService.getBookings(page, size)` method. This response entity will contain
     * the bookings data retrieved based on the provided page and size parameters.
     */
    @GetMapping
    @Operation(summary = "This API is used for getting all Bookings.", description = "Get All Booking")
    public ResponseEntity<?> getBookings(@RequestParam int page, @RequestParam int size) {
        return ResponseEntity.ok(bookingService.getBookings(page, size));
    }

    /**
     * This function retrieves all bookings of a specific user based on the user ID and size parameter.
     *
     * @param userId The `userId` path variable in the `@GetMapping` annotation represents the unique
     *               identifier of the user for whom you want to retrieve bookings.
     * @param size   The `size` parameter in the `getBookingsByUser` method is used to specify the number
     *               of bookings to retrieve for the user with the given `userId`. It is passed as a request parameter
     *               in the API call. The value of `size` determines how many bookings will be returned in
     * @return The method `getBookingsByUser` is returning a `ResponseEntity` object with the booking
     * information for the specified user ID and size.
     */
    @GetMapping("/user/{userId}")
    @Operation(summary = "This API is used for get all Bookings of user.", description = "Get All Booking user specific")
    public ResponseEntity<?> getBookingsByUser(@PathVariable int userId, @RequestParam int size) {
        return ResponseEntity.ok(bookingService.getBookingsByUser(userId, size));
    }

    /**
     * This API endpoint retrieves all bookings of a specific user based on provided filters and size.
     *
     * @param userId  The `userId` parameter in the `@PathVariable` annotation represents the unique
     *                identifier of the user for whom you want to retrieve bookings. This value is extracted from the
     *                URI path of the request URL.
     * @param size    The `size` parameter in the `getBookingsByUser` method is used to specify the number
     *                of bookings to be returned per page. It determines the size of the page when paginating the
     *                bookings for the user with the specified `userId`.
     * @param filters The `filters` parameter in the `getBookingsByUser` method is a `Map<String,
     *                String>` type. This parameter is used to pass key-value pairs of filtering criteria to retrieve
     *                specific bookings for a user. The keys in the map represent the filtering criteria, and the
     *                values represent the
     * @return The method `getBookingsByUser` is returning a `ResponseEntity` object with the result of
     * calling `bookingService.getBookingsByUser(userId, filters, size)`.
     */
    @PostMapping("/user/{userId}/filter")
    @Operation(summary = "This API is used for get all Bookings of user.", description = "Get All Booking user specific")
    public ResponseEntity<?> getBookingsByUser(@PathVariable int userId, @RequestParam int size, @RequestBody Map<String, String> filters) {
        return ResponseEntity.ok(bookingService.getBookingsByUser(userId, filters, size));
    }

    /**
     * This function is a DELETE endpoint in a Java Spring application used for canceling a booking by
     * its ID.
     *
     * @param bookingId The `bookingId` parameter is a Long value representing the unique identifier of
     *                  the booking that needs to be canceled.
     * @return The `cancelBooking` method returns a `ResponseEntity<String>` object, which contains the
     * result of calling the `cancelBooking` method of the `bookingService`.
     */
    @DeleteMapping("/{bookingId}")
    @Operation(summary = "This API is used for canceling booking.", description = "Cancel Booking")
    public ResponseEntity<String> cancelBooking(@PathVariable Long bookingId) {
        return ResponseEntity.ok(bookingService.cancelBooking(bookingId));
    }

}

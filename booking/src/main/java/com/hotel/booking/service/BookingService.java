package com.hotel.booking.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hotel.booking.config.MessageProducer;
import com.hotel.booking.constants.ApplicationConstants;
import com.hotel.booking.dtos.request.CreateBooking;
import com.hotel.booking.entity.Booking;
import com.hotel.booking.entity.Room;
import com.hotel.booking.entity.User;
import com.hotel.booking.enums.BookingStatus;
import com.hotel.booking.exception.CustomException;
import com.hotel.booking.repository.BookingRepo;
import com.hotel.booking.repository.RoomRepo;
import com.hotel.booking.repository.UserRepo;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@Service
public class BookingService {
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private BookingRepo bookingRepo;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private RoomRepo roomRepo;

    @Autowired
    private MessageProducer messageProducer;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private EntityManager entityManager;

    @Value("${booking.topic}")
    private String bookingTopic;


    /**
     * The `bookHotel` function books a hotel room for a user, performing various checks and operations
     * including verifying user and room existence, checking room availability, validating booking
     * price, updating cache, and sending a message about the booking.
     *
     * @param createBooking The `bookHotel` method you provided is responsible for booking a hotel room
     *                      based on the information provided in the `createBooking` object. Here's a breakdown of the
     *                      parameters in the `createBooking` object:
     * @return The `bookHotel` method returns a `Booking` object.
     */
    public Booking bookHotel(CreateBooking createBooking) {
        // check user exists
        User user = userRepo.findById(createBooking.getUserId())
                .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND.value(),"User not found with ID: " + createBooking.getUserId()));

        // check room  exists
        Room room = roomRepo.findById(createBooking.getRoomId())
                .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND.value(),"Room not found with ID: " + createBooking.getRoomId()));

        //checking room availability
        if (!isRoomAvailable(createBooking.getRoomId(), createBooking.getCheckInDate(), createBooking.getCheckOutDate())) {
            throw new CustomException(HttpStatus.NOT_FOUND.value(),"Room not Available with ID: " + createBooking.getRoomId());
        }

        // check booking price is same with room price
        if (createBooking.getTotalPrice() < calculateTotalPrice(createBooking.getCheckInDate(), createBooking.getCheckOutDate(), room.getPricePerNight())) {
            throw new CustomException(HttpStatus.BAD_REQUEST.value(),"Total price provided in booking is less than the calculated total price");
        }
        String cacheKey = ApplicationConstants.ROOM_AVAILABILITY + createBooking.getRoomId();
        String cacheField = createBooking.getCheckInDate() + ApplicationConstants.DASH + createBooking.getCheckOutDate();


        Booking booking = objectMapper.convertValue(createBooking, Booking.class);
        booking.setRoom(room);
        booking.setUser(user);
        booking.setStatus(BookingStatus.BOOKED);
        //changing cache value to false as this is just booked
        redisTemplate.opsForHash().put(cacheKey, cacheField, String.valueOf(false));
        try {
            messageProducer.sendMessage(bookingTopic, objectMapper.writeValueAsString(booking));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        return booking;
    }

    /**
     * The function checks room availability based on booking information and caches the result using
     * Redis.
     *
     * @param roomId       The `roomId` parameter in the `isRoomAvailable` method represents the unique
     *                     identifier of the room for which availability needs to be checked. It is used to identify the
     *                     specific room in the system.
     * @param checkInDate  The `checkInDate` parameter represents the date when a guest is planning to
     *                     check into the room. It is a long value that typically represents a specific point in time, often
     *                     in milliseconds since the Unix epoch. This parameter is used in the `isRoomAvailable` method to
     *                     check the availability of
     * @param checkOutDate The `checkOutDate` parameter in the `isRoomAvailable` method represents the
     *                     date when a guest is expected to check out of the room. This method is used to check the
     *                     availability of a room for booking within a specified date range, from the `checkInDate` to the
     *                     `check
     * @return The method `isRoomAvailable` returns a boolean value indicating whether the specified
     * room is available for booking within the given date range.
     */
    public boolean isRoomAvailable(Long roomId, long checkInDate, long checkOutDate) {

        String cacheKey = ApplicationConstants.ROOM_AVAILABILITY + roomId;
        String cacheField = checkInDate + ApplicationConstants.DASH + checkOutDate;

        // Check if availability information is cached
        Boolean cachedAvailability = redisTemplate.opsForHash().hasKey(cacheKey, cacheField);
        if (cachedAvailability != null && cachedAvailability) {
            // Room availability information found in cache
            return Boolean.parseBoolean((String) redisTemplate.opsForHash().get(cacheKey, cacheField));
        }

        // Retrieve bookings for the specified room within the date range
        List<Booking> bookings = bookingRepo.findByRoomIdAndDateRange(roomId, checkInDate, checkOutDate);

        // Check if any bookings overlap with the requested date range
        boolean isAvailable = bookings.stream()
                .noneMatch(booking -> booking.overlaps(checkInDate, checkOutDate));

        redisTemplate.opsForHash().put(cacheKey, cacheField, String.valueOf(isAvailable));

        return isAvailable;
    }


    /**
     * The function calculates the total price for a stay based on the check-in and check-out
     * timestamps and the price per night.
     *
     * @param checkInMillis  The `checkInMillis` parameter represents the check-in time in milliseconds
     *                       since the epoch.
     * @param checkOutMillis The `checkOutMillis` parameter represents the milliseconds since the epoch
     *                       when the guest checks out of the accommodation.
     * @param pricePerNight  The `pricePerNight` parameter in the `calculateTotalPrice` method
     *                       represents the cost per night for staying at a particular accommodation. This value is
     *                       multiplied by the number of nights stayed to calculate the total price for the entire duration
     *                       of the stay.
     * @return The method `calculateTotalPrice` returns the total price for a stay based on the
     * check-in and check-out dates provided, as well as the price per night.
     */
    public double calculateTotalPrice(long checkInMillis, long checkOutMillis, double pricePerNight) {
        // Convert milliseconds to LocalDate
        LocalDate checkInLocalDate = Instant.ofEpochMilli(checkInMillis).atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate checkOutLocalDate = Instant.ofEpochMilli(checkOutMillis).atZone(ZoneId.systemDefault()).toLocalDate();

        // Calculate duration of stay in nights
        long numberOfNights = checkOutLocalDate.toEpochDay() - checkInLocalDate.toEpochDay();

        // Ensure that the number of nights is non-negative
        if (numberOfNights <= 0) {
            throw new IllegalArgumentException("Invalid check-in and check-out dates");
        }

        // Calculate total price
        return numberOfNights * pricePerNight;
    }

    /**
     * The function `getBookings` retrieves a page of booking records from the database based on the
     * specified page number and size.
     *
     * @param page The `page` parameter in the `getBookings` method represents the page number of results
     *             to retrieve. It is used to specify which page of results to fetch from the database.
     * @param size The `size` parameter in the `getBookings` method represents the number of items (in
     *             this case, bookings) to be included in each page of results when paginating through the data. It
     *             determines how many bookings will be displayed on each page when the method is called.
     * @return A Page of Booking objects is being returned.
     */
    public Page<Booking> getBookings(int page, int size) {
        return bookingRepo.findAll(Pageable.ofSize(size));
    }

    /**
     * The `cancelBooking` function cancels a booking by updating its status to "vacant".
     *
     * @param bookingId The `bookingId` parameter is a unique identifier for a booking that needs to be
     *                  canceled.
     * @return The method `cancelBooking` returns a message "Booking cancel successful" after updating
     * the status of a booking to "vacant".
     */
    public String cancelBooking(long bookingId) {
        Booking booking = bookingRepo.findById(bookingId)
                .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND.value(),"Booking not found with ID: " + bookingId));

        // Update the status to "vacant"
        booking.setStatus(BookingStatus.VACANT);
        return "Booking cancel successful";
    }

    /**
     * The function retrieves a list of bookings for a specific user with a specified size limit.
     *
     * @param userId The `userId` parameter represents the unique identifier of the user for whom you
     *               want to retrieve bookings.
     * @param size   The `size` parameter in the `getBookingsByUser` method represents the number of
     *               bookings to retrieve per page. It is used to limit the number of bookings returned in the result
     *               set.
     * @return A List of Booking objects is being returned.
     */
    public List<Booking> getBookingsByUser(int userId, int size) {
        return bookingRepo.getBookingsByUser(userId, Pageable.ofSize(size));
    }

    /**
     * The function retrieves bookings for a specific user based on given filters and a specified size.
     *
     * @param userId  The `userId` parameter is an integer representing the unique identifier of the user
     *                for whom you want to retrieve bookings.
     * @param filters The `filters` parameter in the `getBookingsByUser` method is a `Map<String,
     *                String>` that contains key-value pairs representing additional conditions to filter the bookings.
     *                Each key in the map corresponds to a column in the `booking` table, and the value represents the
     *                desired value for
     * @param size    The `size` parameter in the `getBookingsByUser` method is used to specify the maximum
     *                number of results to be returned by the query. It determines the size of the result set that will
     *                be retrieved from the database. This parameter helps in limiting the number of bookings that are
     *                returned for
     * @return A list of Booking objects that match the specified user ID and filters.
     */
    public List<Booking> getBookingsByUser(int userId, Map<String, String> filters, int size) {
        StringBuilder query = new StringBuilder("select * from booking b where b.user_id =" + userId);
        filters.forEach((key, value) -> query.append(" AND ").append(key).append("= '").append(value).append("'"));
        return entityManager.createNativeQuery(query.toString(), Booking.class).getResultList();
    }
}

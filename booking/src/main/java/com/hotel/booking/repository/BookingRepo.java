package com.hotel.booking.repository;

import com.hotel.booking.entity.Booking;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingRepo extends JpaRepository<Booking, Long> {
    /**
     * The function retrieves a list of bookings for a specific user with pagination support.
     *
     * @param userId   The `userId` parameter in the method `getBookingsByUser` is used to filter
     *                 bookings based on the user ID. The method retrieves a list of bookings from the database where
     *                 the `user_id` column matches the provided `userId` value.
     * @param pageable The `Pageable` parameter in the `getBookingsByUser` method is used for
     *                 pagination. It allows you to specify the page number, page size, sorting criteria, and more when
     *                 fetching a list of bookings for a specific user. This can be helpful when dealing with a large
     *                 number of
     * @return A list of Booking objects that belong to the user with the specified userId, with
     * pagination applied based on the provided Pageable object.
     */
    @Query(value = "select b.* from booking b where b.user_id =:userId", nativeQuery = true)
    List<Booking> getBookingsByUser(int userId, Pageable pageable);

    /**
     * This function retrieves a list of bookings for a specific room within a given date range.
     *
     * @param roomId          The `roomId` parameter is used to specify the ID of the room for which you want to
     *                        find bookings.
     * @param startDateMillis The `startDateMillis` parameter represents the start date of the date
     *                        range in milliseconds.
     * @param endDateMillis   The `endDateMillis` parameter represents the end date in milliseconds for
     *                        the date range you want to search for bookings.
     * @return This method returns a list of `Booking` entities that match the specified `roomId` and
     * fall within the date range defined by `startDateMillis` and `endDateMillis`. The bookings must
     * have a status of 'BOOKED' and their date range must overlap with the specified date range.
     */
    @Query("SELECT b FROM Booking b WHERE b.room.id = :roomId AND b.status = 'BOOKED' AND ((b.checkInDate <= :endDateMillis) AND (b.checkOutDate >= :startDateMillis))")
    List<Booking> findByRoomIdAndDateRange(Long roomId, long startDateMillis, long endDateMillis);

    /**
     * This function retrieves all bookings with a check-out date before a specified time and with a
     * status of 'BOOKED'.
     *
     * @param time The `time` parameter in the `findAllCheckOutExpire` method represents a timestamp
     *             value. This method is a query method that retrieves a list of `Booking` entities where the
     *             `checkOutDate` is before the specified `time` and the `status` is set to 'BOOKED
     * @return The method is returning a list of Booking entities where the check-out date is before the
     * specified time and the status is 'BOOKED'.
     */
    @Query("select b from Booking b where b.checkOutDate <:time and b.status ='BOOKED'")
    List<Booking> findAllCheckOutExpire(long time);
}

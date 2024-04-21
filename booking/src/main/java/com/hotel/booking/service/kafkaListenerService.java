package com.hotel.booking.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hotel.booking.constants.ApplicationConstants;
import com.hotel.booking.entity.Booking;
import com.hotel.booking.exception.CustomException;
import com.hotel.booking.repository.BookingRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class kafkaListenerService {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private BookingRepo bookingRepo;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    /**
     * This Java function listens to a Kafka topic for booking messages, deserializes them into a
     * Booking object, and saves them to a repository with retry functionality.
     *
     * @param bookingMessage The `bookingMessage` parameter in the `bookingConsumer` method is the
     *                       message received from the Kafka topic "booking-topic". This message is expected to be in a JSON
     *                       format representing a booking, which is then deserialized into a `Booking` object using the
     *                       `objectMapper.readValue` method.
     */
    @RetryableTopic
    @KafkaListener(topics = "${booking.topic}")
    public void bookingConsumer(String bookingMessage) {
        try {
            Booking booking = objectMapper.readValue(bookingMessage, Booking.class);
            bookingRepo.save(booking);
        } catch (Exception e) {
            throw new CustomException(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage());
        }
    }

    /**
     * The `dltHandler` function logs an error message with the provided booking message.
     *
     * @param bookingMessage The `bookingMessage` parameter in the `dltHandler` method is a string that
     *                       contains information related to a booking error.
     */
    @DltHandler
    public void dltHandler(String bookingMessage) {
        try {
            // removing error key from redis as room is still available
            Booking booking = objectMapper.readValue(bookingMessage, Booking.class);
            String redisKeys = ApplicationConstants.ROOM_AVAILABILITY + booking.getRoom().getId();
            redisTemplate.delete(redisKeys);
        } catch (JsonProcessingException e) {
            throw new CustomException(HttpStatus.INTERNAL_SERVER_ERROR.value(),e.getMessage());
        }
        log.info("error with this message : {}", bookingMessage);
    }

}

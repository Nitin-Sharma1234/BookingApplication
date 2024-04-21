package com.hotel.booking.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class MessageProducer {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    /**
     * The `sendMessage` function sends a message to a specified topic using Kafka.
     *
     * @param topic   Topic is a category or feed name to which messages are sent by the producer and from
     *                which messages are received by the consumer. It acts as a channel for communication in Apache
     *                Kafka.
     * @param message The `message` parameter in the `sendMessage` method represents the actual content
     *                or data that you want to send to the specified Kafka topic. It could be any information, text,
     *                JSON, or binary data that you want to publish to the Kafka topic for consumption by subscribers.
     */
    public void sendMessage(String topic, String message) {
        kafkaTemplate.send(topic, message);
    }

}
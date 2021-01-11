package com.wordpress.kkaravitis.kafka.poc.messaging;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;


@Component
public class MessagePublisher {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final String topic;

    public MessagePublisher(@Value("${test.topic}") String topic, KafkaTemplate kafkaTemplate) {
        this.topic = topic;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Transactional
    public void send(String message) {
        kafkaTemplate.send(topic, message);
    }
}

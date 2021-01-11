package com.wordpress.kkaravitis.kafka.poc.messaging;

import java.util.concurrent.CountDownLatch;
import lombok.Getter;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Getter
@Component
public class MessageSubscriber {
    private CountDownLatch latch = new CountDownLatch(1);

    private String payload = null;

    @KafkaListener(topics = "${test.topic}")
    public void receive(ConsumerRecord<?, ?> consumerRecord) {
        payload = consumerRecord.value().toString();
        latch.countDown();
    }
}

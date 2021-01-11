package com.wordpress.kkaravitis.kafka.poc.messaging;

import java.util.HashMap;
import java.util.Map;
import javax.persistence.EntityManagerFactory;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.transaction.ChainedKafkaTransactionManager;
import org.springframework.kafka.transaction.KafkaTransactionManager;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.support.AbstractPlatformTransactionManager;

@EnableKafka
@Configuration()
public class Config {
    @Bean
    public KafkaTransactionManager kafkaTransactionManager(ProducerFactory producerFactory) {
        KafkaTransactionManager ktm = new KafkaTransactionManager(producerFactory);
        ktm.setTransactionSynchronization(AbstractPlatformTransactionManager.SYNCHRONIZATION_ON_ACTUAL_TRANSACTION);
        return ktm;
    }

    @Bean
    @Primary
    public JpaTransactionManager transactionManager(EntityManagerFactory em) {
        return new JpaTransactionManager(em);
    }

    @Bean
    public ChainedKafkaTransactionManager chainedTransactionManager(JpaTransactionManager transactionManager,
        KafkaTransactionManager kafkaTransactionManager) {
        return new ChainedKafkaTransactionManager(kafkaTransactionManager, transactionManager);
    }
}

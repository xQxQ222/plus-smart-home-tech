package ru.yandex.practicum.kafka.configuration;

import jakarta.annotation.PreDestroy;
import lombok.Getter;
import lombok.Setter;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.yandex.practicum.kafka.GeneralAvroSerializer;

import java.util.Properties;

@Getter
@Setter
@Configuration
public class KafkaClientConfiguration {

    @Bean
    KafkaClient getClient() {
        return new KafkaClient() {
            private Consumer<String, SpecificRecordBase> consumer;
            private Producer<String, SpecificRecordBase> producer;

            @Override
            public Producer<String, SpecificRecordBase> getProducer() {
                if (producer == null) {
                    createProducer();
                }
                return producer;
            }

            @Override
            public Consumer<String, SpecificRecordBase> getConsumer() {
                return null;
            }

            @PreDestroy
            @Override
            public void stop() {
                if (consumer != null) {
                    consumer.close();
                }

                if (producer != null) {
                    producer.flush();
                    producer.close();
                }
            }

            private void createProducer() {
                Properties properties = new Properties();
                properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
                properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
                properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, GeneralAvroSerializer.class);
                producer = new KafkaProducer<>(properties);
            }
        };
    }
}

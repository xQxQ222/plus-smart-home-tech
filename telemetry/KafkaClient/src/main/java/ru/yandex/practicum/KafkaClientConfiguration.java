package ru.yandex.practicum;

import jakarta.annotation.PreDestroy;
import lombok.Getter;
import lombok.Setter;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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

            @Value("${kafka.consumer.group-id}")
            private String groupId;

            @Value("${kafka.consumer.client-id}")
            private String clientId;

            @Value("${kafka.bootstrap-servers}")
            private String bootstrapServers;

            @Value("${kafka.producer.key-serializer}")
            private String producerKeySerializer;

            @Value("${kafka.producer.value-serializer}")
            private String producerValueSerializer;

            @Value("${kafka.consumer.key-deserializer}")
            private String consumerKeyDeserializer;

            @Value("${kafka.consumer.value-deserializer}")
            private String consumerValueDeserializer;

            @Override
            public Producer<String, SpecificRecordBase> getProducer() {
                if (producer == null) {
                    createProducer();
                }
                return producer;
            }

            @Override
            public Consumer<String, SpecificRecordBase> getConsumer() {
                if (consumer == null) {
                    createConsumer();
                }
                return consumer;
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
                properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
                properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, producerKeySerializer);
                properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, producerValueSerializer);
                producer = new KafkaProducer<>(properties);
            }

            private void createConsumer() {
                Properties properties = new Properties();
                properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
                properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, consumerKeyDeserializer);
                properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, consumerValueDeserializer);
                properties.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
                properties.put(ConsumerConfig.CLIENT_ID_CONFIG, clientId);
                properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
                properties.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
                consumer = new KafkaConsumer<>(properties);
                Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                    consumer.wakeup();
                }));
            }
        };
    }
}

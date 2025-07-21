package ru.yandex.practicum;

import jakarta.annotation.PreDestroy;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.TopicPartition;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Getter
@Setter
@Configuration
@Slf4j
public class KafkaClientConfiguration {

    @Bean
    KafkaClient getClient() {
        return new KafkaClient() {
            private Producer<String, SpecificRecordBase> producer;

            private final Map<String, Consumer<String, SpecificRecordBase>> consumerMap = new HashMap<>();

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
            public Consumer<String, SpecificRecordBase> getConsumer(String consumerName) {
                if (!consumerMap.containsKey(consumerName)) {
                    createConsumer(consumerName);
                }
                return consumerMap.get(consumerName);
            }

            @Override
            public Consumer<String, SpecificRecordBase> getConsumer(String consumerName, Properties properties) {
                if (!consumerMap.containsKey(consumerName)) {
                    createConsumerWithOptions(consumerName, properties);
                }
                return consumerMap.get(consumerName);
            }

            @PreDestroy
            @Override
            public void stopProducer() {
                if (producer != null) {
                    producer.flush();
                    producer.close();
                }
            }

            @Override
            public synchronized void stopConsumer(String consumerName) {
                if (consumerMap.containsKey(consumerName)) {
                    Consumer<String, SpecificRecordBase> consumer = consumerMap.get(consumerName);
                    if (consumer != null) {
                        consumer.close();
                    }
                }
            }

            @Override
            public void manageOffset(ConsumerRecord<String, ? extends SpecificRecordBase> record, int count, Consumer<String, ? extends SpecificRecordBase> consumer, Map<TopicPartition, OffsetAndMetadata> currentOffsets) {
                currentOffsets.put(
                        new TopicPartition(record.topic(), record.partition()),
                        new OffsetAndMetadata(record.offset() + 1)
                );

                if (count % 10 == 0) {
                    consumer.commitAsync(currentOffsets, (offsets, exception) -> {
                        if (exception != null) {
                            log.warn("Ошибка при попытке коммита offset {}", offsets, exception);
                        }
                    });
                }
            }


            private void createProducer() {
                Properties properties = new Properties();
                properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
                properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, producerKeySerializer);
                properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, producerValueSerializer);
                producer = new KafkaProducer<>(properties);
            }

            private void createConsumer(String name) {
                Properties properties = new Properties();
                properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
                properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, consumerKeyDeserializer);
                properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, consumerValueDeserializer);
                properties.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
                properties.put(ConsumerConfig.CLIENT_ID_CONFIG, clientId);
                properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
                properties.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
                Consumer<String, SpecificRecordBase> consumer = new KafkaConsumer<>(properties);
                Runtime.getRuntime().addShutdownHook(new Thread(consumer::wakeup));
                consumerMap.put(name, consumer);
            }

            private void createConsumerWithOptions(String name, Properties properties) {
                properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
                properties.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
                properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
                Consumer<String, SpecificRecordBase> consumer = new KafkaConsumer<>(properties);
                Runtime.getRuntime().addShutdownHook(new Thread(consumer::wakeup));
                consumerMap.put(name, consumer);
            }
        };
    }
}

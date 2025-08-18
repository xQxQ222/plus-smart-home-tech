package ru.yandex.practicum.kafka;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import ru.yandex.practicum.kafka.deserializer.SnapshotDeserializer;

import java.util.Properties;

public class SnapshotConsumerKafkaProperties {
    public static Properties getProperties() {
        Properties properties = new Properties();
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, SnapshotDeserializer.class);
        properties.put(ConsumerConfig.CLIENT_ID_CONFIG, "snapshot-consumer-processor");
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, "snapshot-processor-group");
        return properties;
    }
}

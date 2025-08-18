package ru.yandex.practicum;

import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.common.TopicPartition;

import java.util.Map;
import java.util.Properties;

public interface KafkaClient {
    Producer<String, SpecificRecordBase> getProducer();

    Consumer<String, SpecificRecordBase> getConsumer(String consumerName);

    Consumer<String, SpecificRecordBase> getConsumer(String consumerName, Properties properties);

    void stopProducer();

    void stopConsumer(String consumerName);

    void manageOffset(ConsumerRecord<String, ? extends SpecificRecordBase> record, int count,
                      Consumer<String, ? extends SpecificRecordBase> consumer,
                      Map<TopicPartition, OffsetAndMetadata> currentOffsets);
}

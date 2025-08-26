package ru.yandex.practicum.starter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.KafkaClient;
import ru.yandex.practicum.KafkaTopicsNames;
import ru.yandex.practicum.service.AggregatorServiceImpl;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
@RequiredArgsConstructor
public class AggregatorStarter {
    private final static Duration POLL_DURATION_TIMEOUT = Duration.ofMillis(1000);
    private final static String CONSUMER_NAME = "aggregator-consumer";

    private final KafkaClient kafkaClient;
    private final KafkaTopicsNames topicsNames;
    private final AggregatorServiceImpl aggregatorService;
    private Producer<String, SpecificRecordBase> producer;
    private Consumer<String, SpecificRecordBase> consumer;
    private final Map<TopicPartition, OffsetAndMetadata> currentOffsets = new HashMap<>();

    public void start() {
        consumer = kafkaClient.getConsumer(CONSUMER_NAME);
        producer = kafkaClient.getProducer();
        try {
            consumer.subscribe(List.of(topicsNames.getSensors()));
            while (true) {
                ConsumerRecords<String, SpecificRecordBase> records = consumer.poll(POLL_DURATION_TIMEOUT);
                int count = 0;
                for (ConsumerRecord<String, SpecificRecordBase> record : records) {
                    aggregatorService.handleRecord(record, producer, topicsNames.getSnapshots());
                    kafkaClient.manageOffset(record, count, consumer, currentOffsets);
                    count++;
                }
                consumer.commitAsync();
            }

        } catch (WakeupException ignored) {
        } catch (Exception e) {
            log.error("Ошибка во время обработки событий от датчиков", e);
        } finally {

            try {
                consumer.commitSync();

            } finally {
                log.info("Закрываем consumer + producer");
                kafkaClient.stopConsumer(CONSUMER_NAME);
            }
        }
    }
}

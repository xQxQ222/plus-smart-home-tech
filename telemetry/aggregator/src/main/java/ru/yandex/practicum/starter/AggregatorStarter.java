package ru.yandex.practicum.starter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.KafkaClient;
import ru.yandex.practicum.KafkaTopicsNames;
import ru.yandex.practicum.service.AggregatorServiceImpl;

import java.time.Duration;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class AggregatorStarter {
    private final static Duration POLL_DURATION_TIMEOUT = Duration.ofMillis(1000);

    private final KafkaClient kafkaClient;
    private Producer<String, SpecificRecordBase> producer;
    private Consumer<String, SpecificRecordBase> consumer;
    private final KafkaTopicsNames topicsNames;
    private final AggregatorServiceImpl aggregatorService;


    public void start() {
        consumer = kafkaClient.getConsumer();
        producer = kafkaClient.getProducer();
        try {
            consumer.subscribe(List.of(topicsNames.getSensors()));
            while (true) {
                ConsumerRecords<String, SpecificRecordBase> records = consumer.poll(POLL_DURATION_TIMEOUT);
                int count = 0;
                for (ConsumerRecord<String, SpecificRecordBase> record : records) {
                    aggregatorService.handleRecord(record, producer, topicsNames.getSnapshots());
                    aggregatorService.manageOffsets(record, count, consumer);
                    count++;
                }
                consumer.commitAsync();
            }

        } catch (WakeupException ignored) {
        } catch (Exception e) {
            log.error("Ошибка во время обработки событий от датчиков", e);
        } finally {

            try {
                producer.flush();
                consumer.commitSync();

            } finally {
                log.info("Закрываем консьюмер");
                consumer.close();
                log.info("Закрываем продюсер");
                producer.close();
            }
        }
    }
}

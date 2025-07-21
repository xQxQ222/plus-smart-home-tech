package ru.yandex.practicum.processor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.KafkaClient;
import ru.yandex.practicum.KafkaTopicsNames;
import ru.yandex.practicum.kafka.SnapshotConsumerKafkaProperties;
import ru.yandex.practicum.service.SnapshotService;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Component
@Slf4j
public class SnapshotProcessor implements Runnable {

    private final static Duration POLL_DURATION_TIMEOUT = Duration.ofMillis(1000);
    private final static String SNAPSHOT_PROCESSOR_CONSUMER = "snapshot-processor";

    private final KafkaClient kafkaClient;
    private final KafkaTopicsNames topicsNames;
    private final Map<TopicPartition, OffsetAndMetadata> currentOffsets = new HashMap<>();
    private Consumer<String, SpecificRecordBase> consumer;
    private final SnapshotService snapshotService;

    @Override
    public void run() {
        try {
            consumer = kafkaClient.getConsumer(SNAPSHOT_PROCESSOR_CONSUMER, SnapshotConsumerKafkaProperties.getProperties());
            consumer.subscribe(List.of(topicsNames.getSnapshots()));
            while (true) {
                ConsumerRecords<String, SpecificRecordBase> records = consumer.poll(POLL_DURATION_TIMEOUT);
                int count = 0;
                for (ConsumerRecord<String, SpecificRecordBase> record : records) {
                    snapshotService.handleRecord(record);
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
                consumer.commitSync(currentOffsets);
            } finally {
                kafkaClient.stopConsumer(SNAPSHOT_PROCESSOR_CONSUMER);
            }
        }


    }
}

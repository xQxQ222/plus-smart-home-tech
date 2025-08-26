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
import ru.yandex.practicum.kafka.telemetry.event.*;
import ru.yandex.practicum.service.HubService;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Component
@Slf4j
public class HubEventProcessor implements Runnable {

    private final static Duration POLL_DURATION_TIMEOUT = Duration.ofMillis(1000);
    private final static String HUB_EVENT_PROCESSOR_CONSUMER = "hub-event-processor";

    private final KafkaClient kafkaClient;
    private final KafkaTopicsNames topicsNames;
    private Consumer<String, SpecificRecordBase> consumer;
    private final Map<TopicPartition, OffsetAndMetadata> currentOffsets = new HashMap<>();
    private final HubService hubService;

    @Override
    public void run() {
        try {
            consumer = kafkaClient.getConsumer(HUB_EVENT_PROCESSOR_CONSUMER);
            consumer.subscribe(List.of(topicsNames.getHubs()));

            while (true) {
                ConsumerRecords<String, SpecificRecordBase> records = consumer.poll(POLL_DURATION_TIMEOUT);
                int count = 0;
                for (ConsumerRecord<String, SpecificRecordBase> record : records) {
                    handleRecord(record);
                    kafkaClient.manageOffset(record, count, consumer, currentOffsets);
                    count++;
                }
                consumer.commitAsync();
            }

        } catch (WakeupException ignored) {
        } catch (Exception e) {
            log.error("Ошибка во время обработки снапшотов", e);
        } finally {
            try {
                consumer.commitSync(currentOffsets);
            } finally {
                log.info("Закрытие consumer+producer");
                kafkaClient.stopConsumer(HUB_EVENT_PROCESSOR_CONSUMER);
            }
        }
    }

    private void handleRecord(ConsumerRecord<String, ? extends SpecificRecordBase> record) {
        HubEventAvro eventAvro = (HubEventAvro) record.value();
        String hubId = eventAvro.getHubId();
        Object payload = eventAvro.getPayload();
        switch (payload) {
            case DeviceAddedEventAvro deviceAddedEventAvro -> hubService.addDevice(deviceAddedEventAvro, hubId);
            case DeviceRemovedEventAvro deviceRemovedEventAvro ->
                    hubService.removeDevice(deviceRemovedEventAvro, hubId);
            case ScenarioAddedEventAvro scenarioAddedEventAvro -> hubService.addScenario(scenarioAddedEventAvro, hubId);
            case ScenarioRemovedEventAvro scenarioRemovedEventAvro ->
                    hubService.removeScenario(scenarioRemovedEventAvro, hubId);
            default -> throw new IllegalStateException("Неизвестный тип " + payload);
        }
    }
}

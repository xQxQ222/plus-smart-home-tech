package ru.yandex.practicum.service.handler.hub;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.ProducerRecord;
import ru.yandex.practicum.kafka.configuration.KafkaClient;
import ru.yandex.practicum.kafka.configuration.KafkaTopicsNames;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.model.hub.HubEvent;
import ru.yandex.practicum.service.handler.HubEventHandler;

@Slf4j
@RequiredArgsConstructor
public abstract class BaseHubEventHandler<T extends SpecificRecordBase> implements HubEventHandler {

    private final KafkaClient kafkaClient;
    private final KafkaTopicsNames topicsNames;

    @Override
    public void handle(HubEvent event) {
        T avroModel = toAvro(event);
        HubEventAvro hubEventAvro = HubEventAvro.newBuilder()
                .setHubId(event.getHubId())
                .setPayload(avroModel)
                .setTimestamp(event.getTimestamp())
                .build();
        kafkaClient.getProducer()
                .send(new ProducerRecord<>(
                        topicsNames.getHubs(),
                        null,
                        event.getTimestamp().toEpochMilli(),
                        event.getHubId(),
                        hubEventAvro));
    }

    protected abstract T toAvro(HubEvent hubEvent);

}

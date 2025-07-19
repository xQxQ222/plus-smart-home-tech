package ru.yandex.practicum.service.handler.hub;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.ProducerRecord;
import ru.yandex.practicum.KafkaClient;
import ru.yandex.practicum.KafkaTopicsNames;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.service.handler.HubEventHandler;

import java.time.Instant;

@Slf4j
@RequiredArgsConstructor
public abstract class BaseHubEventHandler<T extends SpecificRecordBase> implements HubEventHandler {

    private static final int MILLIS_IN_SECOND = 1000;
    private static final int NANOS_IN_MILLIS = 1_000_000;

    private final KafkaClient kafkaClient;
    private final KafkaTopicsNames topicsNames;

    @Override
    public void handle(HubEventProto event) {
        T avroModel = toAvro(event);
        log.trace("Данные события хаба переведены в avro: {}", avroModel);
        HubEventAvro hubEventAvro = HubEventAvro.newBuilder()
                .setHubId(event.getHubId())
                .setPayload(avroModel)
                .setTimestamp(Instant.ofEpochSecond(event.getTimestamp().getSeconds(), event.getTimestamp().getNanos()))
                .build();
        kafkaClient.getProducer()
                .send(new ProducerRecord<>(
                        topicsNames.getHubs(),
                        null,
                        (event.getTimestamp().getSeconds() * MILLIS_IN_SECOND + event.getTimestamp().getNanos() / NANOS_IN_MILLIS),
                        event.getHubId(),
                        hubEventAvro));
    }

    protected abstract T toAvro(HubEventProto hubEvent);

}

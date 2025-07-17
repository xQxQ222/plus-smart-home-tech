package ru.yandex.practicum.service.handler.sensor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.ProducerRecord;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.kafka.configuration.KafkaClient;
import ru.yandex.practicum.kafka.configuration.KafkaTopicsNames;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.service.handler.SensorEventHandler;

import java.time.Instant;

@Slf4j
@RequiredArgsConstructor
public abstract class BaseSensorEventHandler<T extends SpecificRecordBase> implements SensorEventHandler {

    private static final int MILLIS_IN_SECOND = 1000;
    private static final int NANOS_IN_MILLIS = 1_000_000;

    private final KafkaClient kafkaClient;
    private final KafkaTopicsNames topicsNames;

    @Override
    public void handle(SensorEventProto event) {
        T avroData = toAvro(event);
        log.trace("Данные для сенсора переведены в авро: {}", avroData);
        SensorEventAvro sensorEventAvro = SensorEventAvro.newBuilder()
                .setHubId(event.getHubId())
                .setId(event.getId())
                .setPayload(avroData)
                .setTimestamp(Instant.ofEpochSecond(event.getTimestamp().getSeconds(), event.getTimestamp().getNanos()))
                .build();
        kafkaClient.getProducer()
                .send(new ProducerRecord<>(
                        topicsNames.getSensors(),
                        null,
                        (event.getTimestamp().getSeconds() * MILLIS_IN_SECOND + event.getTimestamp().getNanos() / NANOS_IN_MILLIS),
                        event.getHubId(),
                        sensorEventAvro));
    }

    protected abstract T toAvro(SensorEventProto sensorEvent);
}

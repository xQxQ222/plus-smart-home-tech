package ru.yandex.practicum.service.handler.sensor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.ProducerRecord;
import ru.yandex.practicum.kafka.configuration.KafkaClient;
import ru.yandex.practicum.kafka.configuration.KafkaTopicsNames;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.model.sensor.SensorEvent;
import ru.yandex.practicum.service.handler.SensorEventHandler;

@Slf4j
@RequiredArgsConstructor
public abstract class BaseSensorEventHandler<T extends SpecificRecordBase> implements SensorEventHandler {

    private final KafkaClient kafkaClient;
    private final KafkaTopicsNames topicsNames;

    @Override
    public void handle(SensorEvent event) {
        T avroData = toAvro(event);
        SensorEventAvro sensorEventAvro = SensorEventAvro.newBuilder()
                .setHubId(event.getHubId())
                .setId(event.getId())
                .setPayload(avroData)
                .setTimestamp(event.getTimestamp())
                .build();
        kafkaClient.getProducer()
                .send(new ProducerRecord<>(
                        topicsNames.getSensors(),
                        null,
                        event.getTimestamp().toEpochMilli(),
                        event.getHubId(),
                        sensorEventAvro));
    }

    protected abstract T toAvro(SensorEvent sensorEvent);
}

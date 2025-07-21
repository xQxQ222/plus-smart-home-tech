package ru.yandex.practicum.service;

import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.Producer;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

import java.util.Optional;

public interface AggregatorService {
    Optional<SensorsSnapshotAvro> updateState(SensorEventAvro eventAvro);

    void handleRecord(ConsumerRecord<String, SpecificRecordBase> record, Producer<String, SpecificRecordBase> producer, String topic);
}

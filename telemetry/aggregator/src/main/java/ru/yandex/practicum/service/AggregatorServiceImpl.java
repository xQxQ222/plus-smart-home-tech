package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.TopicPartition;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorStateAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AggregatorServiceImpl implements AggregatorService {

    private final Map<TopicPartition, OffsetAndMetadata> currentOffsets = new HashMap<>();
    private final Map<String, SensorsSnapshotAvro> snapshots = new HashMap<>();

    @Override
    public Optional<SensorsSnapshotAvro> updateState(SensorEventAvro eventAvro) {
        String hubId = eventAvro.getHubId();
        if (!snapshots.containsKey(hubId)) {
            SensorsSnapshotAvro sensorsSnapshot = createNewSnapshot(eventAvro);
            snapshots.put(hubId, sensorsSnapshot);
            return Optional.of(sensorsSnapshot);
        } else {
            Optional<SensorsSnapshotAvro> updatedSnapshot = updateSnapshot(eventAvro);
            updatedSnapshot.ifPresent(snapshotAvro -> snapshots.put(hubId, snapshotAvro));
            return updatedSnapshot;
        }
    }

    @Override
    public void manageOffsets(ConsumerRecord<String, SpecificRecordBase> record, int count, Consumer<String, SpecificRecordBase> consumer) {
        currentOffsets.put(
                new TopicPartition(record.topic(), record.partition()),
                new OffsetAndMetadata(record.offset() + 1)
        );

        if (count % 10 == 0) {
            consumer.commitAsync(currentOffsets, (offsets, exception) -> {
                if (exception != null) {
                    log.warn("Error while commiting offsets: {}", offsets, exception);
                }
            });
        }
    }

    @Override
    public void handleRecord(ConsumerRecord<String, SpecificRecordBase> record, Producer<String, SpecificRecordBase> producer, String topic) {
        SensorEventAvro event = (SensorEventAvro) record.value();
        Optional<SensorsSnapshotAvro> snapshot = updateState(event);

        if (snapshot.isPresent()) {
            log.info("Отправляем новый snapshot: {}", snapshot.get());
            producer.send(new ProducerRecord<>(
                    topic,
                    null,
                    event.getTimestamp().toEpochMilli(),
                    event.getHubId(),
                    snapshot.get()
            ));
        }
    }

    private SensorsSnapshotAvro createNewSnapshot(SensorEventAvro event) {
        SensorStateAvro stateAvro = SensorStateAvro.newBuilder()
                .setTimestamp(event.getTimestamp())
                .setData(event.getPayload())
                .build();
        Map<String, SensorStateAvro> sensorStates = new HashMap<>();
        sensorStates.put(event.getId(), stateAvro);
        return SensorsSnapshotAvro.newBuilder()
                .setHubId(event.getHubId())
                .setTimestamp(event.getTimestamp())
                .setSensorsState(sensorStates)
                .build();
    }

    private Optional<SensorsSnapshotAvro> updateSnapshot(SensorEventAvro eventAvro) {
        SensorsSnapshotAvro oldSnapshot = snapshots.get(eventAvro.getHubId());
        String eventId = eventAvro.getId();
        SensorStateAvro oldState = oldSnapshot
                .getSensorsState()
                .get(eventId);
        if (oldState != null) {
            if (oldState.getTimestamp().isAfter(eventAvro.getTimestamp()) || oldState.getData().equals(eventAvro.getPayload())) {
                return Optional.empty();
            }
        }
        SensorStateAvro state = SensorStateAvro.newBuilder()
                .setTimestamp(eventAvro.getTimestamp())
                .setData(eventAvro.getPayload())
                .build();
        oldSnapshot.getSensorsState().put(eventId, state);
        oldSnapshot.setTimestamp(eventAvro.getTimestamp());

        return Optional.of(oldSnapshot);
    }
}

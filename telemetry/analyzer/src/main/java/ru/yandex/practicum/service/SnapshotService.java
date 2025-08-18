package ru.yandex.practicum.service;

import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.consumer.ConsumerRecord;

public interface SnapshotService {
    void handleRecord(ConsumerRecord<String, ? extends SpecificRecordBase> record);
}

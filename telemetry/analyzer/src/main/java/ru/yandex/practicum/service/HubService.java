package ru.yandex.practicum.service;

import ru.yandex.practicum.kafka.telemetry.event.DeviceAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.DeviceRemovedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioRemovedEventAvro;

public interface HubService {
    void addDevice(DeviceAddedEventAvro deviceAdded, String hubId);

    void addScenario(ScenarioAddedEventAvro scenarioAdded, String hubId);

    void removeDevice(DeviceRemovedEventAvro deviceRemoved, String hubId);

    void removeScenario(ScenarioRemovedEventAvro scenarioRemoved, String hubId);
}

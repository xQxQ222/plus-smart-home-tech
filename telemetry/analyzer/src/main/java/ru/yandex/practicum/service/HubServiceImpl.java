package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.kafka.telemetry.event.DeviceAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.DeviceRemovedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioRemovedEventAvro;
import ru.yandex.practicum.model.Action;
import ru.yandex.practicum.model.Condition;
import ru.yandex.practicum.model.Scenario;
import ru.yandex.practicum.model.Sensor;
import ru.yandex.practicum.model.enums.ActionType;
import ru.yandex.practicum.model.enums.ConditionOperation;
import ru.yandex.practicum.model.enums.ConditionType;
import ru.yandex.practicum.repository.ActionRepository;
import ru.yandex.practicum.repository.ConditionRepository;
import ru.yandex.practicum.repository.ScenarioRepository;
import ru.yandex.practicum.repository.SensorRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class HubServiceImpl implements HubService {

    private final SensorRepository sensorRepository;
    private final ScenarioRepository scenarioRepository;
    private final ConditionRepository conditionRepository;
    private final ActionRepository actionRepository;

    @Override
    public void addDevice(DeviceAddedEventAvro deviceAdded, String hubId) {
        String sensorId = deviceAdded.getId();
        if (sensorRepository.findByIdAndHubId(sensorId, hubId).isEmpty()) {
            Sensor sensor = Sensor.builder()
                    .id(sensorId)
                    .hubId(hubId)
                    .build();
            sensorRepository.save(sensor);
            log.trace("Создан новый сенсор: {}", sensor);
        }
    }

    @Override
    public void addScenario(ScenarioAddedEventAvro scenarioAdded, String hubId) {
        Scenario scenario = new Scenario();
        scenario.setHubId(hubId);
        scenario.setName(scenarioAdded.getName());

        List<Condition> conditions = scenarioAdded.getConditions().stream()
                .map(conditionEvent -> Condition.builder()
                        .sensorId(conditionEvent.getSensorId())
                        .type(ConditionType.valueOf(conditionEvent.getType().name()))
                        .operation(ConditionOperation.valueOf(conditionEvent.getOperation().name()))
                        .value(convertToInteger(conditionEvent.getValue()))
                        .scenario(scenario)
                        .build())
                .toList();

        List<Action> actions = scenarioAdded.getActions().stream()
                .map(actionEvent -> Action.builder()
                        .sensorId(actionEvent.getSensorId())
                        .type(ActionType.valueOf(actionEvent.getType().name()))
                        .value(actionEvent.getValue() != null ? actionEvent.getValue() : 0)
                        .scenario(scenario)
                        .build())
                .toList();

        scenario.setConditions(conditions);
        scenario.setActions(actions);
        scenarioRepository.save(scenario);
    }

    private Integer convertToInteger(Object value) {
        if (value instanceof Integer) {
            return (Integer) value;
        } else if (value instanceof Boolean) {
            return (Boolean) value ? 1 : 0;
        } else {
            return null;
        }
    }

    @Override
    public void removeDevice(DeviceRemovedEventAvro deviceRemoved, String hubId) {
        log.trace("Удаление сенсора: {}", deviceRemoved);
        sensorRepository.findByIdAndHubId(deviceRemoved.getId(), hubId).ifPresent(sensorRepository::delete);
    }

    @Override
    public void removeScenario(ScenarioRemovedEventAvro scenarioRemoved, String hubId) {
        log.trace("Удаление сценария: {}", scenarioRemoved);
        scenarioRepository.findByHubIdAndName(hubId, scenarioRemoved.getName()).ifPresent(scenarioRepository::delete);
    }
}

package ru.yandex.practicum.service;

import com.google.protobuf.Timestamp;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.KafkaClient;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.KafkaTopicsNames;
import ru.yandex.practicum.grpc.HubRouterClient;
import ru.yandex.practicum.grpc.telemetry.event.ActionTypeProto;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionProto;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionRequest;
import ru.yandex.practicum.kafka.telemetry.event.*;
import ru.yandex.practicum.model.Action;
import ru.yandex.practicum.model.Condition;
import ru.yandex.practicum.model.Scenario;
import ru.yandex.practicum.model.enums.ConditionOperation;
import ru.yandex.practicum.model.enums.ConditionType;
import ru.yandex.practicum.repository.ScenarioRepository;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SnapshotServiceImpl implements SnapshotService {

    private KafkaClient kafkaClient;
    private KafkaTopicsNames topicsNames;
    private HubRouterClient hubRouterClient;
    private ScenarioRepository scenarioRepository;

    @Override
    public void handleRecord(ConsumerRecord<String, ? extends SpecificRecordBase> record) {
        SensorsSnapshotAvro snapshotAvro = (SensorsSnapshotAvro) record.value();
        String hubId = snapshotAvro.getHubId();
        List<Scenario> scenarios = scenarioRepository.findByHubId(hubId);
        scenarios.stream()
                .filter(scenario -> isScenarioTriggered(scenario, snapshotAvro))
                .forEach(scenario -> executeActions(scenario.getActions(), hubId, snapshotAvro.getTimestamp()));
    }

    private boolean isScenarioTriggered(Scenario scenario, SensorsSnapshotAvro snapshot) {
        for (Condition condition : scenario.getConditions()) {
            if (!checkCondition(condition, snapshot)) {
                return false;
            }
        }
        return true;
    }

    private boolean checkCondition(Condition condition, SensorsSnapshotAvro snapshot) {
        SensorStateAvro sensorState = snapshot.getSensorsState().get(condition.getSensorId());

        if (sensorState == null) {
            return false;
        }
        try {
            return checkConditionSensorState(sensorState, condition);
        } catch (Exception e) {
            log.error("Ошибка при проверке условия {}: {}", condition, e.getMessage());
        }
        return false;
    }

    private boolean evaluateCondition(int sensorValue, ConditionOperation operation, int targetValue) {
        return switch (operation) {
            case EQUALS -> sensorValue == targetValue;
            case GREATER_THAN -> sensorValue > targetValue;
            case LOWER_THAN -> sensorValue < targetValue;
        };
    }

    private boolean checkConditionSensorState(SensorStateAvro sensorState, Condition condition) {
        boolean result = false;
        ConditionType conditionType = condition.getType();
        switch (conditionType) {
            case TEMPERATURE:
                if (sensorState.getData() instanceof TemperatureSensorAvro tempSensor) {
                    result = evaluateCondition(tempSensor.getTemperatureC(), condition.getOperation(), condition.getValue());
                } else if (sensorState.getData() instanceof ClimateSensorAvro climateSensor) {
                    result = evaluateCondition(climateSensor.getTemperatureC(), condition.getOperation(), condition.getValue());
                }
                break;
            case HUMIDITY:
                if (sensorState.getData() instanceof ClimateSensorAvro climateSensor) {
                    result = evaluateCondition(climateSensor.getHumidity(), condition.getOperation(), condition.getValue());
                }
                break;
            case CO2LEVEL:
                if (sensorState.getData() instanceof ClimateSensorAvro climateSensor) {
                    result = evaluateCondition(climateSensor.getCo2Level(), condition.getOperation(), condition.getValue());
                }
                break;
            case LUMINOSITY:
                if (sensorState.getData() instanceof LightSensorAvro lightSensor) {
                    result = evaluateCondition(lightSensor.getLuminosity(), condition.getOperation(), condition.getValue());
                }
                break;
            case MOTION:
                if (sensorState.getData() instanceof MotionSensorAvro motionSensor) {
                    int motionValue = motionSensor.getMotion() ? 1 : 0;
                    result = evaluateCondition(motionValue, condition.getOperation(), condition.getValue());
                }
                break;
            case SWITCH:
                if (sensorState.getData() instanceof SwitchSensorAvro switchSensor) {
                    int switchState = switchSensor.getState() ? 1 : 0;
                    result = evaluateCondition(switchState, condition.getOperation(), condition.getValue());
                }
                break;
            default:
                log.error("Неизвестный тип condition type: {}", condition.getType());
        }
        return result;
    }

    private void executeActions(List<Action> actions, String hubId, Instant timestamp) {
        for (Action action : actions) {
            DeviceActionRequest request = buildRequest(action, timestamp);
            hubRouterClient.getHubRouterClient().handleDeviceAction(request);
        }
    }

    private DeviceActionRequest buildRequest(Action action, Instant timestamp) {

        DeviceActionProto.Builder builderAction = DeviceActionProto.newBuilder();
        Scenario actionScenario = action.getScenario();
        DeviceActionRequest.Builder builder = DeviceActionRequest.newBuilder()
                .setHubId(actionScenario.getHubId())
                .setScenarioName(actionScenario.getName())
                .setAction(builderAction
                        .setSensorId(action.getSensorId())
                        .setType(ActionTypeProto.valueOf(action.getType().name())))
                .setTimestamp(Timestamp.newBuilder()
                        .setSeconds(timestamp.getEpochSecond())
                        .setNanos(timestamp.getNano()));

        boolean actionHasValue = action.getValue() != null;
        if (actionHasValue) {
            builderAction.setValue(action.getValue());
        }

        return builder.build();
    }
}

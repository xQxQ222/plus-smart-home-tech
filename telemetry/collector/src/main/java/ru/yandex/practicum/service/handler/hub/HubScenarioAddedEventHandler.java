package ru.yandex.practicum.service.handler.hub;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionProto;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.grpc.telemetry.event.ScenarioAddedEventProto;
import ru.yandex.practicum.grpc.telemetry.event.ScenarioConditionProto;
import ru.yandex.practicum.kafka.configuration.KafkaClient;
import ru.yandex.practicum.kafka.configuration.KafkaTopicsNames;
import ru.yandex.practicum.kafka.telemetry.event.*;

import java.util.List;

@Component
public class HubScenarioAddedEventHandler extends BaseHubEventHandler<ScenarioAddedEventAvro> {
    public HubScenarioAddedEventHandler(KafkaClient kafkaClient, KafkaTopicsNames topicsNames) {
        super(kafkaClient, topicsNames);
    }

    @Override
    protected ScenarioAddedEventAvro toAvro(HubEventProto hubEvent) {
        ScenarioAddedEventProto event = hubEvent.getScenarioAdded();

        return ScenarioAddedEventAvro.newBuilder()
                .setName(event.getName())
                .setConditions(mapToAvroCondition(event.getConditionList()))
                .setActions(mapToAvroDeviceAction(event.getActionList()))
                .build();
    }

    @Override
    public HubEventProto.PayloadCase getMessageType() {
        return HubEventProto.PayloadCase.SCENARIO_ADDED;
    }

    private List<ScenarioConditionAvro> mapToAvroCondition(List<ScenarioConditionProto> conditions) {
        return conditions.stream()
                .map(condition -> ScenarioConditionAvro.newBuilder()
                        .setSensorId(condition.getSensorId())
                        .setValue(condition.hasBoolValue() ? condition.getBoolValue() : condition.getIntValue())
                        .setOperation(ConditionOperationAvro.valueOf(condition.getOperation().name()))
                        .setType(ConditionTypeAvro.valueOf(condition.getType().name()))
                        .build()).toList();
    }


    private List<DeviceActionAvro> mapToAvroDeviceAction(List<DeviceActionProto> actions) {
        return actions.stream()
                .map(action -> DeviceActionAvro.newBuilder()
                        .setSensorId(action.getSensorId())
                        .setValue(action.getValue())
                        .setType(ActionTypeAvro.valueOf(action.getType().name()))
                        .build()).toList();
    }
}

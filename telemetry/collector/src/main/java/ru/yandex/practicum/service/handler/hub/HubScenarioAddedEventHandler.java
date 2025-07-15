package ru.yandex.practicum.service.handler.hub;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.configuration.KafkaClient;
import ru.yandex.practicum.kafka.configuration.KafkaTopicsNames;
import ru.yandex.practicum.kafka.telemetry.event.*;
import ru.yandex.practicum.model.hub.HubEvent;
import ru.yandex.practicum.model.hub.HubEventType;
import ru.yandex.practicum.model.hub.device.DeviceAction;
import ru.yandex.practicum.model.hub.scenario.ScenarioAddedEvent;
import ru.yandex.practicum.model.hub.scenario.ScenarioCondition;

import java.util.List;

@Component
public class HubScenarioAddedEventHandler extends BaseHubEventHandler<ScenarioAddedEventAvro> {
    public HubScenarioAddedEventHandler(KafkaClient kafkaClient, KafkaTopicsNames topicsNames) {
        super(kafkaClient, topicsNames);
    }

    @Override
    protected ScenarioAddedEventAvro toAvro(HubEvent hubEvent) {
        ScenarioAddedEvent event = (ScenarioAddedEvent) hubEvent;

        return ScenarioAddedEventAvro.newBuilder()
                .setName(event.getName())
                .setConditions(mapToAvroCondition(event.getConditions()))
                .setActions(mapToAvroDeviceAction(event.getActions()))
                .build();
    }

    @Override
    public HubEventType getMessageType() {
        return HubEventType.SCENARIO_ADDED;
    }

    private List<ScenarioConditionAvro> mapToAvroCondition(List<ScenarioCondition> conditions) {
        return conditions.stream()
                .map(condition -> ScenarioConditionAvro.newBuilder()
                        .setSensorId(condition.getSensorId())
                        .setValue(condition.getValue())
                        .setOperation(ConditionOperationAvro.valueOf(condition.getOperation().name()))
                        .setType(ConditionTypeAvro.valueOf(condition.getType().name()))
                        .build()).toList();
    }

    private List<DeviceActionAvro> mapToAvroDeviceAction(List<DeviceAction> actions) {
        return actions.stream()
                .map(action -> DeviceActionAvro.newBuilder()
                        .setSensorId(action.getSensorId())
                        .setValue(action.getValue())
                        .setType(ActionTypeAvro.valueOf(action.getType().name()))
                        .build()).toList();
    }
}

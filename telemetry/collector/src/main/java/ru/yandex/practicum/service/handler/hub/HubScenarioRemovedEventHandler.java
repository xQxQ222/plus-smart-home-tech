package ru.yandex.practicum.service.handler.hub;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.KafkaClient;
import ru.yandex.practicum.KafkaTopicsNames;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.grpc.telemetry.event.ScenarioRemovedEventProto;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioRemovedEventAvro;

@Component
public class HubScenarioRemovedEventHandler extends BaseHubEventHandler<ScenarioRemovedEventAvro> {
    public HubScenarioRemovedEventHandler(KafkaClient kafkaClient, KafkaTopicsNames topicsNames) {
        super(kafkaClient, topicsNames);
    }

    @Override
    protected ScenarioRemovedEventAvro toAvro(HubEventProto hubEvent) {
        ScenarioRemovedEventProto event = hubEvent.getScenarioRemoved();
        return ScenarioRemovedEventAvro.newBuilder()
                .setName(event.getName())
                .build();
    }

    @Override
    public HubEventProto.PayloadCase getMessageType() {
        return HubEventProto.PayloadCase.SCENARIO_REMOVED;
    }
}

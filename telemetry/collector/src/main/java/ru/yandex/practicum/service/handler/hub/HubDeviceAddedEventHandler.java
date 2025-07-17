package ru.yandex.practicum.service.handler.hub;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.DeviceAddedEventProto;
import ru.yandex.practicum.grpc.telemetry.event.DeviceTypeProto;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.kafka.configuration.KafkaClient;
import ru.yandex.practicum.kafka.configuration.KafkaTopicsNames;
import ru.yandex.practicum.kafka.telemetry.event.DeviceAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.DeviceTypeAvro;
import ru.yandex.practicum.model.hub.device.DeviceType;

@Component
public class HubDeviceAddedEventHandler extends BaseHubEventHandler<DeviceAddedEventAvro> {

    public HubDeviceAddedEventHandler(KafkaClient kafkaClient, KafkaTopicsNames kafkaTopicsNames) {
        super(kafkaClient, kafkaTopicsNames);
    }

    @Override
    protected DeviceAddedEventAvro toAvro(HubEventProto hubEvent) {
        DeviceAddedEventProto event = hubEvent.getDeviceAdded();
        return DeviceAddedEventAvro.newBuilder()
                .setId(event.getId())
                .setType(getAvroDeviceType(event.getType()))
                .build();
    }

    @Override
    public HubEventProto.PayloadCase getMessageType() {
        return HubEventProto.PayloadCase.DEVICE_ADDED;
    }

    private DeviceTypeAvro getAvroDeviceType(DeviceType domainDeviceType) {
        return switch (domainDeviceType) {
            case DeviceType.CLIMATE_SENSOR -> DeviceTypeAvro.CLIMATE_SENSOR;
            case DeviceType.LIGHT_SENSOR -> DeviceTypeAvro.LIGHT_SENSOR;
            case DeviceType.MOTION_SENSOR -> DeviceTypeAvro.MOTION_SENSOR;
            case DeviceType.SWITCH_SENSOR -> DeviceTypeAvro.SWITCH_SENSOR;
            case DeviceType.TEMPERATURE_SENSOR -> DeviceTypeAvro.TEMPERATURE_SENSOR;
        };
    }

    private DeviceTypeAvro getAvroDeviceType(DeviceTypeProto domainDeviceType) {
        return switch (domainDeviceType) {
            case DeviceTypeProto.CLIMATE_SENSOR -> DeviceTypeAvro.CLIMATE_SENSOR;
            case DeviceTypeProto.LIGHT_SENSOR -> DeviceTypeAvro.LIGHT_SENSOR;
            case DeviceTypeProto.MOTION_SENSOR -> DeviceTypeAvro.MOTION_SENSOR;
            case DeviceTypeProto.SWITCH_SENSOR -> DeviceTypeAvro.SWITCH_SENSOR;
            case DeviceTypeProto.TEMPERATURE_SENSOR -> DeviceTypeAvro.TEMPERATURE_SENSOR;
            case UNRECOGNIZED -> null;
        };
    }
}

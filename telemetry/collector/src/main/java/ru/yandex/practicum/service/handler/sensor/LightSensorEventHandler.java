package ru.yandex.practicum.service.handler.sensor;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.KafkaClient;
import ru.yandex.practicum.KafkaTopicsNames;
import ru.yandex.practicum.grpc.telemetry.event.LightSensorProto;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.kafka.telemetry.event.LightSensorAvro;

@Component
public class LightSensorEventHandler extends BaseSensorEventHandler<LightSensorAvro> {
    public LightSensorEventHandler(KafkaClient kafkaClient, KafkaTopicsNames topicsNames) {
        super(kafkaClient, topicsNames);
    }

    @Override
    protected LightSensorAvro toAvro(SensorEventProto sensorEvent) {
        LightSensorProto event = sensorEvent.getLightSensorEvent();
        return LightSensorAvro.newBuilder()
                .setLinkQuality(event.getLinkQuality())
                .setLuminosity(event.getLuminosity())
                .build();
    }

    @Override
    public SensorEventProto.PayloadCase getMessageType() {
        return SensorEventProto.PayloadCase.LIGHT_SENSOR_EVENT;
    }
}

package ru.yandex.practicum.controller;

import com.google.protobuf.Empty;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.grpc.telemetry.collector.CollectorControllerGrpc;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.service.handler.HubEventHandler;
import ru.yandex.practicum.service.handler.SensorEventHandler;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@RestController
@Slf4j
@RequestMapping("/events")
@GrpcService
public class EventController extends CollectorControllerGrpc.CollectorControllerImplBase {

    private final Map<HubEventProto.PayloadCase, HubEventHandler> hubEventHandlersMap;
    private final Map<SensorEventProto.PayloadCase, SensorEventHandler> sensorEventHandlersMap;

    public EventController(Set<HubEventHandler> hubEventHandlers, Set<SensorEventHandler> sensorEventHandlers) {
        this.hubEventHandlersMap = hubEventHandlers.stream()
                .collect(Collectors.toMap(HubEventHandler::getMessageType, Function.identity()));
        this.sensorEventHandlersMap = sensorEventHandlers.stream()
                .collect(Collectors.toMap(SensorEventHandler::getMessageType, Function.identity()));
    }

    @PostMapping("/sensors")
    @ResponseStatus(HttpStatus.OK)
    public void sensorEvent(SensorEventProto sensorEvent, StreamObserver<Empty> observer) {
        log.info("Получен POST запрос /events/sensors типа {} с телом: {}", sensorEvent.getPayloadCase(), sensorEvent);
        if (sensorEventHandlersMap.containsKey(sensorEvent.getPayloadCase())) {
            sensorEventHandlersMap.get(sensorEvent.getPayloadCase()).handle(sensorEvent);
            log.info("POST запрос /events/sensors успешно обработан!");
        } else {
            log.error("Ошибка! Неизвестный тип события для сенсора: {}", sensorEvent.getPayloadCase());
            throw new IllegalArgumentException("Неизвестный тип события");
        }
    }

    @PostMapping("/hubs")
    @ResponseStatus(HttpStatus.OK)
    public void hubEvent(HubEventProto hubEvent, StreamObserver<Empty> observer) {
        log.info("Получен POST запрос /events/hubs типа {} с телом: {}", hubEvent.getPayloadCase(), hubEvent);
        if (hubEventHandlersMap.containsKey(hubEvent.getPayloadCase())) {
            hubEventHandlersMap.get(hubEvent.getPayloadCase()).handle(hubEvent);
            log.info("POST запрос /events/hubs успешно обработан!");
        } else {
            log.error("Ошибка! Неизвестный тип события для хаба: {}", hubEvent.getPayloadCase());
            throw new IllegalArgumentException("Неизвестный тип события");
        }
    }
}

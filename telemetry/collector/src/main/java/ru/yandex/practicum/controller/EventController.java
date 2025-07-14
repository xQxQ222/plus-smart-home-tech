package ru.yandex.practicum.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.model.hub.HubEvent;
import ru.yandex.practicum.model.hub.HubEventType;
import ru.yandex.practicum.model.sensor.SensorEvent;
import ru.yandex.practicum.model.sensor.SensorEventType;
import ru.yandex.practicum.service.handler.HubEventHandler;
import ru.yandex.practicum.service.handler.SensorEventHandler;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@RestController
@Slf4j
@RequestMapping("/events")
public class EventController {

    private final Map<HubEventType, HubEventHandler> hubEventHandlersMap;
    private final Map<SensorEventType, SensorEventHandler> sensorEventHandlersMap;

    public EventController(Set<HubEventHandler> hubEventHandlers, Set<SensorEventHandler> sensorEventHandlers) {
        this.hubEventHandlersMap = hubEventHandlers.stream()
                .collect(Collectors.toMap(HubEventHandler::getMessageType, Function.identity()));
        this.sensorEventHandlersMap = sensorEventHandlers.stream()
                .collect(Collectors.toMap(SensorEventHandler::getMessageType, Function.identity()));
    }

    @PostMapping("/sensors")
    @ResponseStatus(HttpStatus.OK)
    public void sensorEvent(@Valid @RequestBody SensorEvent sensorEvent) {
        log.info("Получен POST запрос /events/sensors типа {} с телом: {}", sensorEvent.getType(), sensorEvent);
        if (sensorEventHandlersMap.containsKey(sensorEvent.getType())) {
            sensorEventHandlersMap.get(sensorEvent.getType()).handle(sensorEvent);
            log.info("POST запрос /events/sensors успешно обработан!");
        } else {
            log.error("Ошибка! Неизвестный тип события для сенсора: {}", sensorEvent.getType());
            throw new IllegalArgumentException("Неизвестный тип события");
        }
    }

    @PostMapping("/hubs")
    @ResponseStatus(HttpStatus.OK)
    public void hubEvent(@Valid @RequestBody HubEvent hubEvent) {
        log.info("Получен POST запрос /events/hubs типа {} с телом: {}", hubEvent.getType(), hubEvent);
        if (hubEventHandlersMap.containsKey(hubEvent.getType())) {
            hubEventHandlersMap.get(hubEvent.getType()).handle(hubEvent);
            log.info("POST запрос /events/hubs успешно обработан!");
        } else {
            log.error("Ошибка! Неизвестный тип события для хаба: {}", hubEvent.getType());
            throw new IllegalArgumentException("Неизвестный тип события");
        }
    }
}

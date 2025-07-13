package ru.yandex.practicum.model.hub.scenario;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ScenarioCondition {
    private String sensorId;
    private ScenarioConditionType type;
    private ScenarioOperationType operation;
    private int value;
}

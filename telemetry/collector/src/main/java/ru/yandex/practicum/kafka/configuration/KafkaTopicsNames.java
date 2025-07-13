package ru.yandex.practicum.kafka.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "kafka.topic.telemetry")
public class KafkaTopicsNames {
    private String sensors;
    private String hubs;
}

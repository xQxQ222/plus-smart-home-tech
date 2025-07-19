package ru.yandex.practicum;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import ru.yandex.practicum.starter.AggregatorStarter;

@SpringBootApplication
public class Aggregator {
    public static void main(String[] args){
        ConfigurableApplicationContext context = SpringApplication.run(Aggregator.class, args);
        AggregatorStarter aggregator = context.getBean(AggregatorStarter.class);
        aggregator.start();
    }
}

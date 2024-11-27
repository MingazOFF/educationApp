package ru.t1.educationApp.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
@Primary
public class KafkaTaskProducer {

    private final KafkaTemplate template;

    public void send(String topic, Object o) {
        try {
            template.send(topic, o).get();
            template.flush();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

}

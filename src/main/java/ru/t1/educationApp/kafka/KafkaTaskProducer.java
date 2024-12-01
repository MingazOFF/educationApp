package ru.t1.educationApp.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import ru.t1.educationApp.aspect.LogAfterThrowing;
import ru.t1.educationApp.aspect.LogBefore;
import ru.t1.educationApp.dto.NotificationTaskDto;

@Slf4j
@Component
@RequiredArgsConstructor
@Primary
public class KafkaTaskProducer {

    private final KafkaTemplate<String, NotificationTaskDto> template;

    @LogBefore
    @LogAfterThrowing
    public void send(String topic, NotificationTaskDto notificationTaskDto) {
        try {
            template.send(topic, notificationTaskDto);

            template.flush();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

}

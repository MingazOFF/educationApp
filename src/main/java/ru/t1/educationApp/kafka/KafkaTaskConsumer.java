package ru.t1.educationApp.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Before;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import ru.t1.educationApp.aspect.LogAfterThrowing;
import ru.t1.educationApp.aspect.LogBefore;
import ru.t1.educationApp.dto.NotificationTaskDto;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaTaskConsumer {

    @LogBefore
    @LogAfterThrowing
    @KafkaListener(id = "t1_demo",
            topics = "t1_task_updated",
            containerFactory = "kafkaListenerContainerFactory")
    public void listener(@Payload NotificationTaskDto notificationTaskDto,
                         Acknowledgment ack,
                         @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                         @Header(KafkaHeaders.RECEIVED_KEY) String key) {

        log.info("Task consumer: Processing new messages");

        try {

            System.out.println("Send email with: " + notificationTaskDto);

        } catch (Exception e) {
            log.warn(e.getMessage());
        } finally {
            ack.acknowledge();
        }

        log.info("Client consumer: Messages have been processed");
    }


}

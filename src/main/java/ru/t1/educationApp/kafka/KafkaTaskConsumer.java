package ru.t1.educationApp.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import ru.t1.educationApp.aspect.LogAfterThrowing;
import ru.t1.educationApp.aspect.LogBefore;
import ru.t1.educationApp.dto.NotificationTaskDto;
import ru.t1.educationApp.service.NotificationEmailService;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaTaskConsumer {

    private final NotificationEmailService notificationEmailService;

    @LogBefore
    @LogAfterThrowing
    @KafkaListener(id = "${spring.t1.kafka.consumer.group-id}",
            topics = "${spring.t1.kafka.topic.task_status}",
            containerFactory = "kafkaListenerContainerFactory")
    public void listener(@Payload List<NotificationTaskDto> notificationTaskDtoList,
                         Acknowledgment ack,
                         @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                         @Header(KafkaHeaders.RECEIVED_KEY) String key) {

        log.info("Task consumer: Processing new messages");

        try {
            notificationTaskDtoList.forEach(notificationEmailService::sendNotification);
            ack.acknowledge();
        } catch (Exception e) {
            log.warn("Exception: {}", e.getMessage());
        }

        log.info("Task consumer: Messages have been processed");
    }


}

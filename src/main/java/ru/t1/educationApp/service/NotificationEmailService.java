package ru.t1.educationApp.service;


import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import ru.t1.educationApp.aspect.LogAfterThrowing;
import ru.t1.educationApp.aspect.LogBefore;
import ru.t1.educationApp.dto.NotificationTaskDto;

@Service
@RequiredArgsConstructor
public class NotificationEmailService {

    @Value("${spring.mail.address}")
    private String emailAddress;

    private final JavaMailSender javaMailSender;

    @LogBefore
    @LogAfterThrowing
    public void sendNotification(NotificationTaskDto notificationTaskDto) {
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setTo(emailAddress);
        simpleMailMessage.setSubject("Task Status Updated");
        simpleMailMessage.setText("Task ID: " + notificationTaskDto.getId() +
                "\nNew status: " + notificationTaskDto.getStatus().toString());

        try {
            javaMailSender.send(simpleMailMessage);
        } catch (MailException e) {
            throw new RuntimeException(e);
        }


    }
}

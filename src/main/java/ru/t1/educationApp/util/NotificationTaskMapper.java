package ru.t1.educationApp.util;

import ru.t1.educationApp.dto.NotificationTaskDto;
import ru.t1.educationApp.entity.Task;

public class NotificationTaskMapper {

    public static NotificationTaskDto EntityToNotificationTaskDto(Task task) {
        return NotificationTaskDto.builder()
                .id(task.getId())
                .status(task.getStatus())
                .build();
    }
}

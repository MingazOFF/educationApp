package ru.t1.educationApp.util;

import org.springframework.stereotype.Component;
import ru.t1.educationApp.dto.TaskDto;
import ru.t1.educationApp.entity.Task;

@Component
public class TaskMapper {

    public static Task dtoToEntity(TaskDto taskDto) {
        return Task.builder()
                .title(taskDto.getTitle())
                .description(taskDto.getDescription())
                .userId(taskDto.getUserId())
                .status(taskDto.getStatus())
                .build();
    }

    public static TaskDto entityToDto(Task task) {
        return TaskDto.builder()
                .title(task.getTitle())
                .description(task.getDescription())
                .userId(task.getUserId())
                .status(task.getStatus())
                .build();
    }
}

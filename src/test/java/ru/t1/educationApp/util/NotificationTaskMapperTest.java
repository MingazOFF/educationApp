package ru.t1.educationApp.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.t1.educationApp.dto.NotificationTaskDto;
import ru.t1.educationApp.entity.Task;
import ru.t1.educationApp.entity.TaskStatusEnum;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
@DisplayName("Тест преобразования в notificationDto")
class NotificationTaskMapperTest {

    @Test
    @DisplayName("Тест преобразования task в notificationTaskDto")
    void entityToNotificationTaskDtoTest() {
        //given
        Task task = Task.builder()
                .id(101)
                .title("some_title")
                .description("some_description")
                .userId(3)
                .status(TaskStatusEnum.COMPLETED)
                .build();
        //when
        NotificationTaskDto notificationTaskDto = NotificationTaskMapper.entityToNotificationTaskDto(task);
        //then
        assertNotNull(notificationTaskDto);
        assertEquals(task.getId(), notificationTaskDto.getId());
        assertEquals(task.getStatus(), notificationTaskDto.getStatus());
    }
}
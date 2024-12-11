package ru.t1.educationApp.unitTests.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.t1.educationApp.dto.TaskDto;
import ru.t1.educationApp.entity.Task;
import ru.t1.educationApp.entity.TaskStatusEnum;
import ru.t1.educationApp.util.TaskMapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
@DisplayName("Тест преобразования в taskDto и обратно")
class TaskMapperTest {


    @Test
    @DisplayName("Тест преобразования taskDto в task")
    void dtoToEntityTest() {
        //given
        TaskDto taskDto = TaskDto.builder()
                .title("any_title")
                .description("any_description")
                .userId(777)
                .status(TaskStatusEnum.OPEN)
                .build();
        //when
        Task taskFromMapper = TaskMapper.dtoToEntity(taskDto);
        //then
        assertNotNull(taskFromMapper);
        assertEquals(taskDto.getTitle(), taskFromMapper.getTitle());
        assertEquals(taskDto.getDescription(), taskFromMapper.getDescription());
        assertEquals(taskDto.getUserId(), taskFromMapper.getUserId());
        assertEquals(taskDto.getStatus(), taskFromMapper.getStatus());
    }

    @Test
    @DisplayName("Тест преобразования task в taskDto")
    void entityToDtoTest() {
        //given
        Task task = Task.builder()
                .id(101)
                .title("some_title")
                .description("some_description")
                .userId(3)
                .status(TaskStatusEnum.COMPLETED)
                .build();
        //when
        TaskDto taskDtoFromMapper = TaskMapper.entityToDto(task);
        //then
        assertNotNull(taskDtoFromMapper);
        assertEquals(task.getTitle(), taskDtoFromMapper.getTitle());
        assertEquals(task.getDescription(), taskDtoFromMapper.getDescription());
        assertEquals(task.getUserId(), taskDtoFromMapper.getUserId());
        assertEquals(task.getStatus(), taskDtoFromMapper.getStatus());
    }
}
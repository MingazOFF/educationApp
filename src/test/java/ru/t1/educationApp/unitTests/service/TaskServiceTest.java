package ru.t1.educationApp.unitTests.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.t1.educationApp.dto.NotificationTaskDto;
import ru.t1.educationApp.dto.TaskDto;
import ru.t1.educationApp.entity.Task;
import ru.t1.educationApp.entity.TaskStatusEnum;
import ru.t1.educationApp.exception.TaskNotFoundException;
import ru.t1.educationApp.kafka.KafkaTaskProducer;
import ru.t1.educationApp.repository.TaskRepository;
import ru.t1.educationApp.service.TaskService;
import ru.t1.educationApp.util.TaskMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Тест сервисного слоя")
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private KafkaTaskProducer producer;


    @Test
    @DisplayName("Тест возвращения id при сохранении task")
    void createTaskTest() {
        //given
        TaskService taskService = new TaskService(taskRepository, producer);

        TaskDto taskDto = new TaskDto("any_title", "any_description", 777, TaskStatusEnum.OPEN);
        Task task = new Task(10, "any_title", "any_description", 777, TaskStatusEnum.OPEN);
        when(taskRepository.save(any(Task.class))).thenReturn(task);

        try (MockedStatic<TaskMapper> taskMapperMockedStatic = mockStatic(TaskMapper.class)) {
            when(TaskMapper.dtoToEntity(taskDto)).thenReturn(task);
            //when
            int returningId = taskService.createTask(taskDto);
            //then
            assertEquals(10, returningId);
        }
    }

    @Test
    @DisplayName("Тест возвращения task по id")
    void getTaskByIdTest() {
        //given
        TaskService taskService = new TaskService(taskRepository, producer);
        int id = 10;

        Task task = new Task(id, "any_title", "any_description", 777, TaskStatusEnum.OPEN);
        TaskDto taskDto = new TaskDto("any_title", "any_description", 777, TaskStatusEnum.OPEN);
        when(taskRepository.findById(id)).thenReturn(Optional.of(task));
        try (MockedStatic<TaskMapper> taskMapperMockedStatic = mockStatic(TaskMapper.class)) {
            when(TaskMapper.entityToDto(task)).thenReturn(taskDto);
            //when
            TaskDto taskDtoFromService = taskService.getTaskById(id);
            //then
            assertNotNull(taskDtoFromService);
            assertEquals(taskDto.getTitle(), taskDtoFromService.getTitle());
            assertEquals(taskDto.getDescription(), taskDtoFromService.getDescription());
            assertEquals(taskDto.getUserId(), taskDtoFromService.getUserId());
            assertEquals(taskDto.getStatus(), taskDtoFromService.getStatus());
        }
    }

    @Test
    @DisplayName("Тест выброса исключения при запросе task с несуществующим id")
    void getTaskByIdErrorTest() {
        //given
        int id = 404;
        TaskService taskService = new TaskService(taskRepository, producer);
        when(taskRepository.findById(404)).thenReturn(Optional.empty());
        //when
        //then
        assertThrows(TaskNotFoundException.class, () -> {
            taskService.getTaskById(404);
        });
    }

    @Test
    @DisplayName("Тест обновления task без отправки в топик")
    void updateTaskTest() {
        //given
        TaskService taskService = new TaskService(taskRepository, producer);
        int id = 102;
        Task taskFromDB = new Task(id, "some_title", "some_description", 99, TaskStatusEnum.CANCELLED);
        TaskDto taskDtoForUpdate = new TaskDto("another_title", "another_description", 99, TaskStatusEnum.CANCELLED);
        Task updatedTask = new Task(id, "another_title", "another_description", 99, TaskStatusEnum.CANCELLED);
        when(taskRepository.findById(id)).thenReturn(Optional.of(taskFromDB));
        when(taskRepository.save(any(Task.class))).thenReturn(updatedTask);

        try (MockedStatic<TaskMapper> taskMapperMockedStatic = mockStatic(TaskMapper.class)) {
            when(TaskMapper.entityToDto(updatedTask)).thenReturn(taskDtoForUpdate);
            //when
            TaskDto updatedTaskDto = taskService.updateTask(id, taskDtoForUpdate);
            //then
            assertNotNull(updatedTaskDto);
            assertSame(taskDtoForUpdate, updatedTaskDto);
            verifyNoInteractions(producer);
        }
    }

    @Test
    @DisplayName("Тест обновления task c отправкой в топик")
    void updateTaskAndSendTest() {
        //given
        TaskService taskService = new TaskService(taskRepository, producer);
        int id = 102;
        Task taskFromDB = new Task(id, "some_title", "some_description", 99, TaskStatusEnum.CANCELLED);
        TaskDto taskDtoForUpdate = new TaskDto("another_title", "another_description", 99, TaskStatusEnum.OPEN);
        Task updatedTask = new Task(id, "another_title", "another_description", 99, TaskStatusEnum.OPEN);
        when(taskRepository.findById(id)).thenReturn(Optional.of(taskFromDB));
        when(taskRepository.save(any(Task.class))).thenReturn(updatedTask);

        try (MockedStatic<TaskMapper> taskMapperMockedStatic = mockStatic(TaskMapper.class)) {
            when(TaskMapper.entityToDto(updatedTask)).thenReturn(taskDtoForUpdate);
            //when
            TaskDto updatedTaskDto = taskService.updateTask(id, taskDtoForUpdate);
            //then
            assertNotNull(updatedTaskDto);
            assertSame(taskDtoForUpdate, updatedTaskDto);
            verify(producer).send(null, new NotificationTaskDto(id, updatedTaskDto.getStatus()));
        }
    }

    @Test
    @DisplayName("Тест выброса исключения при попытке обновлении task с несуществующим id")
    void updateTaskErrorTest() {
        //given
        TaskService taskService = new TaskService(taskRepository, producer);
        int id = 102;
        TaskDto taskDtoForUpdate = new TaskDto("another_title", "another_description", 99, TaskStatusEnum.OPEN);
        when(taskRepository.findById(id)).thenReturn(Optional.empty());
        //when
        //then
        assertThrows(TaskNotFoundException.class, () -> {
            taskService.updateTask(id, taskDtoForUpdate);
        });
    }


    @Test
    @DisplayName("Тест удаления task по указанному id")
    void deleteTaskTest() {
        //given
        TaskService taskService = new TaskService(taskRepository, producer);
        int id = 103;
        when(taskRepository.existsById(id)).thenReturn(true);
        //when
        taskService.deleteTask(id);
        //then
        verify(taskRepository).deleteById(id);

    }

    @Test
    @DisplayName("Тест выброса исключения при попытке удаления task по несущестсвующему id")
    void deleteTaskErrorTest() {
        //given
        TaskService taskService = new TaskService(taskRepository, producer);
        int id = 107;
        when(taskRepository.existsById(id)).thenReturn(false);
        //when
        //then
        assertThrows(TaskNotFoundException.class, () -> {
            taskService.deleteTask(id);
        });


    }


    @Test
    @DisplayName("Тест получения всех task")
    void getAllTasksTest() {
        //given
        TaskService taskService = new TaskService(taskRepository, producer);
        Task task1 = new Task(1, "some_title", "some_description", 99, TaskStatusEnum.CANCELLED);
        Task task2 = new Task(2, "any_title", "any_description", 777, TaskStatusEnum.OPEN);
        TaskDto taskDto1 = new TaskDto("some_title", "some_description", 99, TaskStatusEnum.CANCELLED);
        TaskDto taskDto2 = new TaskDto("any_title", "any_description", 777, TaskStatusEnum.OPEN);
        List<Task> listEntity = new ArrayList<>();
        listEntity.add(task1);
        listEntity.add(task2);
        List<TaskDto> listDto = new ArrayList<>();
        listDto.add(taskDto1);
        listDto.add(taskDto2);
        when(taskRepository.findAll()).thenReturn(listEntity);
        try (MockedStatic<TaskMapper> taskMapperMockedStatic = mockStatic(TaskMapper.class)) {
            when(TaskMapper.entityToDto(task1)).thenReturn(taskDto1);
            when(TaskMapper.entityToDto(task2)).thenReturn(taskDto2);
            //when
            List<TaskDto> taskDtoList = taskService.getAllTasks();
            //then
            assertEquals(listDto, taskDtoList);
        }
    }
}
package ru.t1.educationApp.integrationTests.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import ru.t1.educationApp.dto.NotificationTaskDto;
import ru.t1.educationApp.dto.TaskDto;
import ru.t1.educationApp.entity.Task;
import ru.t1.educationApp.entity.TaskStatusEnum;
import ru.t1.educationApp.kafka.KafkaTaskProducer;
import ru.t1.educationApp.repository.TaskRepository;
import ru.t1.educationApp.util.TaskMapper;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@DisplayName("Тест taskController интеграционный")
public class TaskControllerTest extends AbstractContainerBaseTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private KafkaTaskProducer producer;


    static Task task1;
    static Task task2;


    @BeforeEach
    void resetDatabase() throws Exception {
        taskRepository.deleteAll();

        try (Connection connection = DriverManager.getConnection(
                postgres.getJdbcUrl(),
                postgres.getUsername(),
                postgres.getPassword());
             Statement statement = connection.createStatement()) {

            statement.execute("ALTER SEQUENCE task_id_seq RESTART WITH 1;");
        }
        task1 = new Task(1, "some_title", "some_description", 99, TaskStatusEnum.CANCELLED);
        task2 = new Task(2, "any_title", "any_description", 20, TaskStatusEnum.OPEN);
        taskRepository.save(task1);
        taskRepository.save(task2);
    }


    @Test
    @DisplayName("Тест запроса task по id")
    void getTaskByIdTest() throws Exception {
        //given
        int id = task1.getId();
        //when
        ResultActions resultActions = mockMvc.perform(get("/tasks/" + id));
        //then
        resultActions
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.title", is(task1.getTitle())))
                .andExpect(jsonPath("$.description", is(task1.getDescription())))
                .andExpect(jsonPath("$.user_id", is(task1.getUserId())))
                .andExpect(jsonPath("$.status", is(task1.getStatus().toString())));

    }

    @Test
    @DisplayName("Тест запроса task по не существующему id")
    void getTaskByIdErrorTest() throws Exception {
        //given
        int id = 3;
        //when
        ResultActions resultActions = mockMvc.perform(get("/tasks/" + id));
        //then
        resultActions
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message", is("Task(id=" + id + ") not found")));

    }

    @Test
    @DisplayName("Тест запроса для создания task ")
    void createTaskTest() throws Exception {
        //given
        TaskDto taskDto = TaskMapper.entityToDto(task2);
        //when
        ResultActions resultActions = mockMvc.perform(post("/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(taskDto)));
        //then
        resultActions
                .andExpect(status().isOk())
                .andExpect(content().string("3"));
    }

    @Test
    @DisplayName("Тест запроса для обновления task без изменения статуса")
    void updateTaskTest() throws Exception {
        //given
        int id = task1.getId();
        TaskDto taskDto = TaskMapper.entityToDto(task1);
        taskDto.setUserId(7777);
        //when
        ResultActions resultActions = mockMvc.perform(put("/tasks/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(taskDto)));
        //then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is(taskDto.getTitle())))
                .andExpect(jsonPath("$.description", is(taskDto.getDescription())))
                .andExpect(jsonPath("$.user_id", is(taskDto.getUserId())))
                .andExpect(jsonPath("$.status", is(taskDto.getStatus().toString())));

        verifyNoInteractions(producer);
    }

    @Test
    @DisplayName("Тест запроса для обновления task c изменением статуса")
    void updateTaskNewStatusTest() throws Exception {
        //given
        int id = task1.getId();
        TaskDto taskDto = TaskMapper.entityToDto(task1);
        taskDto.setStatus(TaskStatusEnum.COMPLETED);
        //when
        ResultActions resultActions = mockMvc.perform(put("/tasks/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(taskDto)));
        //then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is(taskDto.getTitle())))
                .andExpect(jsonPath("$.description", is(taskDto.getDescription())))
                .andExpect(jsonPath("$.user_id", is(taskDto.getUserId())))
                .andExpect(jsonPath("$.status", is(taskDto.getStatus().toString())));

        verify(producer).send("task_status_topic", new NotificationTaskDto(id, taskDto.getStatus()));
    }

    @Test
    @DisplayName("Тест запроса для обновления task c не существующим id")
    void updateTaskErrorTest() throws Exception {
        //given
        int id = 3;
        TaskDto taskDto = TaskMapper.entityToDto(task1);
        taskDto.setStatus(TaskStatusEnum.COMPLETED);
        //when
        ResultActions resultActions = mockMvc.perform(put("/tasks/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(taskDto)));
        //then
        resultActions
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message", is("Task(id=" + id + ") not found")));


    }

    @Test
    @DisplayName("Тест запроса для удаления task")
    void deleteTaskTest() throws Exception {
        //given
        int id = 1;

        //when
        ResultActions resultActions = mockMvc.perform(delete("/tasks/" + id));
        //then
        resultActions
                .andExpect(status().isOk());

    }

    @Test
    @DisplayName("Тест запроса для удаления task c не существующим id")
    void deleteTaskErrorTest() throws Exception {
        //given
        int id = 3;

        //when
        ResultActions resultActions = mockMvc.perform(delete("/tasks/" + id));
        //then
        resultActions
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message", is("Task(id=" + id + ") not found")));

    }


    @Test
    @DisplayName("Тест запроса получения всех task")
    void getAllTaskTest() throws Exception {
        //given

        //when
        ResultActions resultActions = mockMvc.perform(get("/tasks"));
        //then
        resultActions
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].title", is(task1.getTitle())))
                .andExpect(jsonPath("$[0].description", is(task1.getDescription())))
                .andExpect(jsonPath("$[0].user_id", is(task1.getUserId())))
                .andExpect(jsonPath("$[0].status", is(task1.getStatus().toString())))
                .andExpect(jsonPath("$[1].title", is(task2.getTitle())))
                .andExpect(jsonPath("$[1].description", is(task2.getDescription())))
                .andExpect(jsonPath("$[1].user_id", is(task2.getUserId())))
                .andExpect(jsonPath("$[1].status", is(task2.getStatus().toString())));
    }

}

package ru.t1.educationApp.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.t1.educationApp.aspect.LogBefore;
import ru.t1.educationApp.dto.TaskDto;
import ru.t1.educationApp.service.TaskService;

import java.util.List;


@AllArgsConstructor
@RestController
@RequestMapping("/tasks")
public class TaskController {

    private final TaskService taskService;

    @LogBefore
    @PostMapping
    public int createTask(@RequestBody TaskDto taskDto) {
        return taskService.createTask(taskDto);
    }

    @LogBefore
    @GetMapping("/{id}")
    public TaskDto getTaskDtoById(@PathVariable int id) {
        return taskService.getTaskById(id);
    }

    @LogBefore
    @PutMapping("/{id}")
    public TaskDto updateTask(@PathVariable int id, @RequestBody TaskDto taskDto) {
        return taskService.updateTask(id, taskDto);
    }

    @LogBefore
    @DeleteMapping("/{id}")
    public void deleteTaskById(@PathVariable int id) {
        taskService.deleteTask(id);
    }

    @LogBefore
    @GetMapping
    public List<TaskDto> getAllTaskDto() {
        return taskService.getAllTasks();
    }

}

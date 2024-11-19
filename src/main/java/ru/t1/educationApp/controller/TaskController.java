package ru.t1.educationApp.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.t1.educationApp.aspect.LogBefore;
import ru.t1.educationApp.entity.Task;
import ru.t1.educationApp.exception.TaskNotFoundException;
import ru.t1.educationApp.service.TaskService;

import java.util.List;


@AllArgsConstructor
@RestController
@RequestMapping("/tasks")
public class TaskController {

    private final TaskService taskService;

    @LogBefore
    @PostMapping
    public int createTask(@RequestBody Task task) {
        return taskService.createTask(task);
    }

    @LogBefore
    @GetMapping("/{id}")
    public ResponseEntity<?> getTaskById(@PathVariable int id) {
        try {
            Task task = taskService.getTaskById(id);
            return ResponseEntity.ok(task);
        } catch (TaskNotFoundException e) {
            return ResponseEntity.status(404).build();
        }

    }

    @LogBefore
    @PutMapping("/{id}")
    public ResponseEntity<?> updateTask(@PathVariable int id, @RequestBody Task task) {
        try {
            Task updatedTask = taskService.updateTask(id, task);
            return ResponseEntity.ok(updatedTask);
        } catch (TaskNotFoundException e) {
            return ResponseEntity.status(404).build();
        }
    }

    @LogBefore
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTaskById(@PathVariable int id) {
        try {
            taskService.deleteTask(id);
            return ResponseEntity.ok().build();
        } catch (TaskNotFoundException e) {
            return ResponseEntity.status(404).build();
        }

    }

    @LogBefore
    @GetMapping
    public List<Task> getAllTasks() {
        return taskService.getAllTasks();
    }

}

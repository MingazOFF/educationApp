package ru.t1.educationApp.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.t1.educationApp.aspect.LogAfterReturning;
import ru.t1.educationApp.aspect.LogAfterThrowing;
import ru.t1.educationApp.aspect.LogAround;
import ru.t1.educationApp.aspect.LogBefore;
import ru.t1.educationApp.entity.Task;
import ru.t1.educationApp.exception.TaskNotFoundException;
import ru.t1.educationApp.repository.TaskRepository;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class TaskService {
    private final TaskRepository taskRepository;

    public int createTask(Task task) {
        return taskRepository.save(task).getId();
    }

    @LogBefore
    @LogAfterThrowing
    @LogAfterReturning
    public Task getTaskById(int id) {
        Optional<Task> optionalTask = taskRepository.findById(id);
        return optionalTask.orElseThrow(
                () -> new TaskNotFoundException("Task(id=" + id + ") not found"));
    }

    @LogBefore
    @LogAfterThrowing
    @LogAfterReturning
    public Task updateTask(int id, Task task) {
        Task taskFromDB = taskRepository.findById(id).orElseThrow(
                () -> new TaskNotFoundException("Task(id=" + id + ") not found"));
        taskFromDB.setTitle(task.getTitle());
        taskFromDB.setDescription(task.getDescription());
        taskFromDB.setUserId(task.getUserId());
        return taskRepository.save(taskFromDB);
    }

    @LogBefore
    @LogAfterThrowing
    @LogAfterReturning
    public void deleteTask(int id) {
        boolean taskIsExists = taskRepository.existsById(id);
        if (taskIsExists) {
            taskRepository.deleteById(id);
        } else {
            throw new TaskNotFoundException("Task(id=" + id + ") not found");
        }
    }

    @LogAround
    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }
}

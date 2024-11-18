package ru.t1.educationApp.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.t1.educationApp.entity.Task;
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

    public Task getTaskById(int id) {
        Optional<Task> optionalTask = taskRepository.findById(id);
        return optionalTask.orElse(null);
    }

    public Task updateTask(int id, Task task) {
        Task taskFromDB = taskRepository.findById(id).get();
        taskFromDB.setTitle(task.getTitle());
        taskFromDB.setDescription(task.getDescription());
        taskFromDB.setUserId(task.getUserId());
        return taskRepository.save(taskFromDB);
    }

    public void deleteTask(int id) {
        taskRepository.deleteById(id);
    }

    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }
}
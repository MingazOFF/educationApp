package ru.t1.educationApp.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.t1.educationApp.aspect.LogAfterReturning;
import ru.t1.educationApp.aspect.LogAfterThrowing;
import ru.t1.educationApp.aspect.LogAround;
import ru.t1.educationApp.aspect.LogBefore;
import ru.t1.educationApp.dto.TaskDto;
import ru.t1.educationApp.entity.Task;
import ru.t1.educationApp.exception.TaskNotFoundException;
import ru.t1.educationApp.repository.TaskRepository;
import ru.t1.educationApp.util.TaskMapper;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class TaskService {
    private final TaskRepository taskRepository;

    public int createTask(TaskDto taskDto) {
        Task task = TaskMapper.dtoToEntity(taskDto);
        return taskRepository.save(task).getId();
    }

    @LogBefore
    @LogAfterThrowing
    @LogAfterReturning
    public TaskDto getTaskById(int id) {
        Optional<Task> optionalTask = taskRepository.findById(id);
        if (optionalTask.isEmpty()) {
            throw new TaskNotFoundException("Task(id=" + id + ") not found");
        }
        TaskDto taskDto = TaskMapper.entityToDto(optionalTask.get());
        return taskDto;
    }

    @LogBefore
    @LogAfterThrowing
    @LogAfterReturning
    public TaskDto updateTask(int id, TaskDto taskDto) {
        Task taskFromDB = taskRepository.findById(id).orElseThrow(
                () -> new TaskNotFoundException("Task(id=" + id + ") not found"));

        taskFromDB.setTitle(taskDto.getTitle());
        taskFromDB.setDescription(taskDto.getDescription());
        taskFromDB.setUserId(taskDto.getUserId());
        return TaskMapper.entityToDto(taskRepository.save(taskFromDB));
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
    public List<TaskDto> getAllTasks() {
        return taskRepository.findAll().stream()
                .map(TaskMapper::entityToDto)
                .toList();
    }
}

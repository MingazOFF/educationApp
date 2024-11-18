package ru.t1.educationApp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.t1.educationApp.entity.Task;

public interface TaskRepository extends JpaRepository<Task, Integer> {
}

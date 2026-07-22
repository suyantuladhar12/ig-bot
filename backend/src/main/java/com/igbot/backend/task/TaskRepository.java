package com.igbot.backend.task;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.*;

public interface TaskRepository extends JpaRepository<Task, Long> {
    Optional<Task> findByTitle(String title);

    List<Task> findByDone(boolean done);
}
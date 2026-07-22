package com.igbot.backend.task;

import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class TaskService {
    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository){
        this.taskRepository = taskRepository;
    }

    public List<Task> findAll(){
        return taskRepository.findAll();
    }

    public Task findById(Long id){
        return taskRepository.findById(id).orElseThrow(() -> new NoSuchElementException("Task not found" + id));
    }

    public Task findByName(String name){
        return taskRepository.findByTitle(name).orElseThrow(() -> new NoSuchElementException("Title not found" + name));
    };

    public List<Task > doneOrNot(boolean done){
        return taskRepository.findByDone(done);
    }

}

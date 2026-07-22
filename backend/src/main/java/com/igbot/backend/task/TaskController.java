package com.igbot.backend.task;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping("/Tasks")
    public ResponseEntity<List<Task>> getAllDoneTasks(
        @RequestParam("done") boolean done){
        return ResponseEntity.ok(taskService.doneOrNot(done));
    }

}

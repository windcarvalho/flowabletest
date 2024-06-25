package com.example.demo.controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.Article;
import com.example.demo.service.BigFlowThreeService;

@RestController
public class BigFlowThreeController {
@Autowired
    private BigFlowThreeService service;

    @GetMapping("/startbig3")
    public void start() {        
        service.startProcess();
    }

    @PostMapping("/deploybig3/{fileName}")
    public void post(@PathVariable(name = "fileName") String fileName) throws IOException {
        System.out.println("Filename - Big3:"+fileName);
        service.deployFlow(fileName);
    }

    @PostMapping("/startbig3/{key}")
    public void start(@PathVariable(name = "key") String key){
        service.startProcessByKey(key);
    }

    @PostMapping("/completebig3/{taskId}")
    public void complete(@PathVariable(name = "taskId") String taskId) {
        service.completeTask(taskId);
    }

    @GetMapping("/tasksbig3")
    public List<Map<String, Object>> getTasks(@RequestParam String assignee) {
        return service.getTasks(assignee);
    }

 

    @GetMapping("/allbig3")
    public ResponseEntity<?> getAllTasks() {
        return ResponseEntity.ok(service.allTasks());
    }
}

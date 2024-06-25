package com.example.demo.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.task.api.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.model.Article;

@Service
public class BigFlowThreeService {
  @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private TaskService taskService;

    @Transactional
    public void startProcess() {
        Map<String, Object> variables = new HashMap<>();
        variables.put("pass", true);
        variables.put("yes", "ssss"); 
        variables.put("vixe", "ssss");          
        System.out.println("Dando start no processo com 3 variáveis");     
        runtimeService.startProcessInstanceByKey("processBigFlows",variables);
    }

    public void startProcessByKey(String key) {
        //runtimeService.startProcessInstanceByKey(key);
    }

    public void completeTask(String taskId) {
        System.out.println("Número de variáveis da: "+taskId+" é "+taskService.getVariables(taskId).size());  
        if(taskService.getVariables(taskId).size()>0){
            System.out.println("valor :"+taskService.getVariables(taskId).values());
        }      
        System.out.println("Completando a tarefa: "+taskId);
        taskService.complete(taskId);
        
    }

    @Transactional
    public List<Map<String, Object>> getTasks(String assignee) {
        System.out.println("Listando as tarefas");
        List<Task> tasks = taskService.createTaskQuery()
                .taskCandidateGroup(assignee)
                .list();
                System.out.println("Existem ainda "+tasks.size() +" tarefas");
        return tasks.stream()
                .map(task -> {
                    Map<String, Object> variables = taskService.getVariables(task.getId());
                    return variables;
                })
                .collect(Collectors.toList());
    }


    @Transactional
    public void deployFlow(String fileName) throws IOException {
//        BpmnModel model = new BpmnModel();
//        repositoryService.createDeployment()
//                .addBytes(file.getName(), file.getBytes())
//                .deploy();
        //System.out.println("Filename Hello:"+fileName);
        repositoryService.createDeployment()
                .addClasspathResource(fileName)
                .deploy();
    }

   
    public List<Object> allTasks() {
        var responseList = new ArrayList<>();

        
        
        var tasks = taskService.createTaskQuery()
                .list().stream().toList();
        System.out.println("Listando as tarefas");
        System.out.println("Existem ativas ainda "+tasks.size() +" tarefas");
        System.out.println("Existem ainda "+taskService.createTaskQuery().count() +" tarefas");
        tasks.stream()
                .map(task -> {
                    var responseMap = new HashMap<>();

                    responseMap.put("id", task.getId());
                    responseMap.put("name", task.getName());

                    responseList.add(responseMap);
                    return null;
                }).collect(Collectors.toList());

        return responseList;
    }
}

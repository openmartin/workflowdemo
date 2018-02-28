package com.example.workflowdemo.service;

import com.example.workflowdemo.domain.Leave;
import org.activiti.engine.IdentityService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class LeaveWorkflowService {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private UserService userService;

    @Autowired
    private LeaveService leaveService;

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private IdentityService identityService;

    public ProcessInstance startWorkflow(Leave leave, long userId, Map<String, Object> variables) {
        if (leave.getLeaveId() == 0) {
            leave.setApplyTime(new Date());
            leave.setUserId(userId);
        }
        leaveService.insert(leave);

        long businessKey = leave.getLeaveId();

        identityService.setAuthenticatedUserId(String.valueOf(userId));
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("leave",
                String.valueOf(businessKey), variables);
        String processInstanceId = processInstance.getId();
        leave.setProcessInstanceId(processInstanceId);
        leaveService.update(leave);

        logger.debug("start process of {key={}, bkey={}, pid={}, variables={}}", new Object[]{"leave", businessKey, processInstanceId, variables});

        return processInstance;
    }

    public List<Leave> findTodoTasks(long userId) {
        List<Leave> results = new ArrayList<Leave>();
        List<Task> tasks = new ArrayList<Task>();

        // 根据当前人的ID查询
        List<Task> todoList = taskService.createTaskQuery().processDefinitionKey("leave").
                taskAssignee(String.valueOf(userId)).list();

        // 根据当前人未签收的任务
        List<Task> unsignedTasks = taskService.createTaskQuery().processDefinitionKey("leave").
                taskCandidateUser(String.valueOf(userId)).list();

        // 合并
        tasks.addAll(todoList);
        tasks.addAll(unsignedTasks);

        // 根据流程的业务ID查询实体并关联
        for (Task task : tasks) {
            String processInstanceId = task.getProcessInstanceId();
            ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
            String businessKey = processInstance.getBusinessKey();
            Leave leave = leaveService.get(new Long(businessKey));
            leave.setTask(task);
            leave.setProcessInstance(processInstance);
            String processDefinitionId = processInstance.getProcessDefinitionId();
            ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().processDefinitionId(processDefinitionId).singleResult();
            leave.setProcessDefinition(processDefinition);
            results.add(leave);
        }

        return results;
    }

    public void complete(Leave leave, Boolean save, String taskId, Map<String, Object> variables) {
        if (save) {
            leaveService.update(leave);
        }
        taskService.complete(taskId, variables);
    }



}

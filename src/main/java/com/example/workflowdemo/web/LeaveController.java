package com.example.workflowdemo.web;

import com.example.workflowdemo.domain.Leave;
import com.example.workflowdemo.domain.User;
import com.example.workflowdemo.service.LeaveService;
import com.example.workflowdemo.service.LeaveWorkflowService;
import com.example.workflowdemo.service.UserService;
import com.example.workflowdemo.util.ActivityUtil;
import com.example.workflowdemo.util.Page;
import com.example.workflowdemo.util.PageUtil;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.engine.*;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.impl.RepositoryServiceImpl;
import org.activiti.engine.impl.bpmn.behavior.SubProcessActivityBehavior;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.pvm.PvmTransition;
import org.activiti.engine.impl.pvm.ReadOnlyProcessDefinition;
import org.activiti.engine.impl.pvm.delegate.ActivityBehavior;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.repository.ProcessDefinitionQuery;
import org.activiti.engine.runtime.Execution;
import org.activiti.engine.runtime.NativeExecutionQuery;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.activiti.image.ProcessDiagramGenerator;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.jws.WebParam;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.*;

@Controller
public class LeaveController {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private UserService userService;

    @Autowired
    private LeaveService leaveService;

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private LeaveWorkflowService leaveWorkflowService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private IdentityService identityService;

    @Autowired
    ProcessEngineConfiguration processEngineConfiguration;

    @Autowired
    private HistoryService historyService;

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateFormat.setLenient(false);
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
    }

    @RequestMapping(value = "/task/leave/apply")
    public String createForm(Model model) {
        model.addAttribute("leave", new Leave());
        return "task/leaveApply";
    }

    @RequestMapping(value = "/task/leave/start", method = RequestMethod.POST)
    public String startWorkflow(Leave leave, RedirectAttributes redirectAttributes) {
        try {
            User user = userService.getLocalUser();
            Map<String, Object> variables = new HashMap<String, Object>();
            ProcessInstance processInstance = leaveWorkflowService.startWorkflow(leave, user.getUserId(), variables);
            redirectAttributes.addFlashAttribute("message", "流程已启动，流程ID：" + processInstance.getId());
        } catch (ActivitiException e) {
            if (e.getMessage().indexOf("no processes deployed with key") != -1) {
                logger.warn("没有部署流程!", e);
                redirectAttributes.addFlashAttribute("error", "没有部署请假流程");
            } else {
                logger.error("启动请假流程失败：", e);
                redirectAttributes.addFlashAttribute("error", "系统内部错误！");
            }
        } catch (Exception e) {
            logger.error("启动请假流程失败：", e);
            redirectAttributes.addFlashAttribute("error", "系统内部错误！");
        }
        return "redirect:/task/leave/apply";
    }

    /**
     * 任务列表
     *
     * @param leave
     */
    @RequestMapping(value = "/task/list")
    public ModelAndView taskList(HttpSession session) {
        ModelAndView mav = new ModelAndView("task/taskList");
        User user = userService.getLocalUser();
        List<Leave> results = leaveWorkflowService.findTodoTasks(user.getUserId());
        mav.addObject("records", results);
        return mav;
    }

    /**
     * 签收任务
     */
    @RequestMapping(value = "task/claim/{id}")
    public String claim(@PathVariable("id") String taskId, HttpSession session, RedirectAttributes redirectAttributes) {
        User user = userService.getLocalUser();
        taskService.claim(taskId, String.valueOf(user.getUserId()));
        redirectAttributes.addFlashAttribute("message", "任务已签收");
        return "redirect:/task/list";
    }

    /**
     * 任务列表
     *
     * @param leave
     */
    @RequestMapping(value = "task/view/{taskId}")
    public ModelAndView showTaskView(@PathVariable("taskId") String taskId) {
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        String processInstanceId = task.getProcessInstanceId();
        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
        Leave leave = leaveService.get(new Long(processInstance.getBusinessKey()));

        ModelAndView mav = new ModelAndView("task/" + task.getDescription());
        mav.addObject("leave", leave);
        mav.addObject("task", task);
        return mav;
    }



    /**
     * 完成任务
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "task/complete/{id}", method = {RequestMethod.POST, RequestMethod.GET})
    @SuppressWarnings("unchecked")
    public String complete(@PathVariable("id") String taskId, @RequestParam(value = "saveEntity", required = false) String saveEntity,
                           @ModelAttribute("preloadLeave") Leave leave, HttpServletRequest request, RedirectAttributes redirectAttributes) {
        boolean saveEntityBoolean = BooleanUtils.toBoolean(saveEntity);
        Map<String, Object> variables = new HashMap<String, Object>();
        Enumeration<String> parameterNames = request.getParameterNames();
        try {
            while (parameterNames.hasMoreElements()) {
                String parameterName = (String) parameterNames.nextElement();
                if (parameterName.startsWith("p_")) {
                    // 参数结构：p_B_name，p为参数的前缀，B为类型，name为属性名称
                    String[] parameter = parameterName.split("_");
                    if (parameter.length == 3) {
                        String paramValue = request.getParameter(parameterName);
                        Object value = paramValue;
                        if (parameter[1].equals("B")) {
                            value = BooleanUtils.toBoolean(paramValue);
                        } else if (parameter[1].equals("DT")) {
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                            value = sdf.parse(paramValue);
                        }
                        variables.put(parameter[2], value);
                    } else {
                        throw new RuntimeException("invalid parameter for activiti variable: " + parameterName);
                    }
                }
            }
            leaveWorkflowService.complete(leave, saveEntityBoolean, taskId, variables);
            redirectAttributes.addFlashAttribute("message", "任务已完成");
        } catch (Exception e) {
            logger.error("error on complete task {}, variables={}", new Object[]{taskId, variables, e});
            request.setAttribute("error", "完成任务失败");
        }
        return "redirect:/task/list";
    }

    /**
     * 自动绑定页面字段
     */
    @ModelAttribute("preloadLeave")
    public Leave getLeave(@RequestParam(value = "id", required = false) Long id, HttpSession session) {
        if (id != null) {
            return leaveService.get(id);
        }
        return new Leave();
    }


    @RequestMapping("/task/involved")
    public ModelAndView list(HttpServletRequest request) {
        ModelAndView mav = new ModelAndView("task/involved");
    /* 标准查询
    List<ProcessInstance> processInstanceList = runtimeService.createProcessInstanceQuery().list();
    List<Execution> list = runtimeService.createExecutionQuery().list();
    mav.addObject("list", list);
    */

        User user = userService.getLocalUser();
        Page<Execution> page = new Page<Execution>(20);
        int[] pageParams = PageUtil.init(page, request);
        NativeExecutionQuery nativeExecutionQuery = runtimeService.createNativeExecutionQuery();

        // native query
        String sql = "SELECT * FROM (\n" +
                "SELECT \n" +
                "    RES.*, ART.START_TIME_\n" +
                "FROM\n" +
                "    ACT_RU_EXECUTION RES\n" +
                "        LEFT JOIN\n" +
                "    ACT_HI_TASKINST ART ON ART.PROC_INST_ID_ = RES.PROC_INST_ID_\n" +
                "WHERE\n" +
                "    ART.ASSIGNEE_ = #{userId}\n" +
                "UNION\n" +
                "SELECT RES.*, APT.START_TIME_\n" +
                "FROM\n" +
                "    ACT_RU_EXECUTION RES\n" +
                "    LEFT JOIN\n" +
                "    ACT_HI_PROCINST APT ON APT.PROC_INST_ID_ = RES.PROC_INST_ID_\n" +
                "    WHERE\n" +
                "    APT.START_USER_ID_ = #{userId}\n" +
                "    ) AS T\n" +
                "ORDER BY START_TIME_ DESC";

        nativeExecutionQuery.parameter("userId", user.getUserId());

        List<Execution> executionList = nativeExecutionQuery.sql(sql).listPage(pageParams[0], pageParams[1]);

        // 查询流程定义对象
        Map<String, ProcessDefinition> definitionMap = new HashMap<String, ProcessDefinition>();

        // 任务的英文-中文对照
        Map<String, Task> taskMap = new HashMap<String, Task>();

        // 每个Execution的当前活动ID，可能为多个
        Map<String, List<String>> currentActivityMap = new HashMap<String, List<String>>();

        // 设置每个Execution对象的当前活动节点
        for (Execution execution : executionList) {
            ExecutionEntity executionEntity = (ExecutionEntity) execution;
            String processInstanceId = executionEntity.getProcessInstanceId();
            String processDefinitionId = executionEntity.getProcessDefinitionId();

            // 缓存ProcessDefinition对象到Map集合
            definitionCache(definitionMap, processDefinitionId);

            // 查询当前流程的所有处于活动状态的活动ID，如果并行的活动则会有多个
            List<String> activeActivityIds = runtimeService.getActiveActivityIds(execution.getId());
            currentActivityMap.put(execution.getId(), activeActivityIds);

            for (String activityId : activeActivityIds) {

                // 查询处于活动状态的任务
                Task task = taskService.createTaskQuery().taskDefinitionKey(activityId).executionId(execution.getId()).singleResult();

                // 调用活动
                if (task == null) {
                    ProcessInstance processInstance = runtimeService.createProcessInstanceQuery()
                            .superProcessInstanceId(processInstanceId).singleResult();
                    task = taskService.createTaskQuery().processInstanceId(processInstance.getProcessInstanceId()).singleResult();
                    definitionCache(definitionMap, processInstance.getProcessDefinitionId());
                }
                taskMap.put(activityId, task);
            }
        }

        mav.addObject("taskMap", taskMap);
        mav.addObject("definitions", definitionMap);
        mav.addObject("currentActivityMap", currentActivityMap);

        page.setResult(executionList);
        page.setTotalCount(nativeExecutionQuery.sql("select count(*) from (" + sql + ") as t").count());
        mav.addObject("page", page);

        return mav;
    }

    /**
     * 流程定义对象缓存
     *
     * @param definitionMap
     * @param processDefinitionId
     */
    private void definitionCache(Map<String, ProcessDefinition> definitionMap, String processDefinitionId) {
        if (definitionMap.get(processDefinitionId) == null) {
            ProcessDefinitionQuery processDefinitionQuery = repositoryService.createProcessDefinitionQuery();
            processDefinitionQuery.processDefinitionId(processDefinitionId);
            ProcessDefinition processDefinition = processDefinitionQuery.singleResult();

            // 放入缓存
            definitionMap.put(processDefinitionId, processDefinition);
        }
    }


    @RequestMapping("/task/trace/{executionId}")
    public void readResource(@PathVariable("executionId") String executionId, HttpServletResponse response) throws IOException {
        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceId(executionId).singleResult();
        BpmnModel bpmnModel = repositoryService.getBpmnModel(processInstance.getProcessDefinitionId());
        ProcessDefinitionEntity processDefinition = (ProcessDefinitionEntity) repositoryService.createProcessDefinitionQuery().processDefinitionId(processInstance.getProcessDefinitionId()).singleResult();
        List<String> activeActivityIds = runtimeService.getActiveActivityIds(executionId);
        ProcessDiagramGenerator diagramGenerator = processEngineConfiguration.getProcessDiagramGenerator();
        List<String> highLightedFlows = getHighLightedFlows(processDefinition, processInstance.getId());
        InputStream imageStream =diagramGenerator.generateDiagram(bpmnModel, "png", activeActivityIds, highLightedFlows,
                processEngineConfiguration.getActivityFontName(), processEngineConfiguration.getLabelFontName(),
                processEngineConfiguration.getAnnotationFontName(), null, 1.0);

        // 输出资源内容到相应对象
        byte[] b = new byte[1024];
        int len;
        while ((len = imageStream.read(b, 0, 1024)) != -1) {
            response.getOutputStream().write(b, 0, len);
        }
    }

    private List<String> getHighLightedFlows(ProcessDefinitionEntity processDefinition, String processInstanceId) {
        List<String> highLightedFlows = new ArrayList<String>();
        List<HistoricActivityInstance> historicActivityInstances = historyService
                .createHistoricActivityInstanceQuery()
                .processInstanceId(processInstanceId)
                .orderByHistoricActivityInstanceStartTime().asc().list();

        List<String> historicActivityInstanceList = new ArrayList<String>();
        for (HistoricActivityInstance hai : historicActivityInstances) {
            historicActivityInstanceList.add(hai.getActivityId());
        }

        // add current activities to list
        List<String> highLightedActivities = runtimeService.getActiveActivityIds(processInstanceId);
        historicActivityInstanceList.addAll(highLightedActivities);

        // activities and their sequence-flows
        for (ActivityImpl activity : processDefinition.getActivities()) {
            int index = historicActivityInstanceList.indexOf(activity.getId());

            if (index >= 0 && index + 1 < historicActivityInstanceList.size()) {
                List<PvmTransition> pvmTransitionList = activity
                        .getOutgoingTransitions();
                for (PvmTransition pvmTransition : pvmTransitionList) {
                    String destinationFlowId = pvmTransition.getDestination().getId();
                    if (destinationFlowId.equals(historicActivityInstanceList.get(index + 1))) {
                        highLightedFlows.add(pvmTransition.getId());
                    }
                }
            }
        }
        return highLightedFlows;
    }


}

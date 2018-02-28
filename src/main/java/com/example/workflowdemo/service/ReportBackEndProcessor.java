package com.example.workflowdemo.service;

import com.example.workflowdemo.domain.Leave;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * 销假后处理器
 * <p>
 * 设置销假时间
 * </p>
 * <p>
 * 使用Spring代理，可以注入Bean，管理事物
 * </p>
 *
 * @author HenryYan
 */
@Component
@Transactional
public class ReportBackEndProcessor implements TaskListener {

    private static final long serialVersionUID = 1L;

    @Autowired
    LeaveService leaveService;

    /*
     * (non-Javadoc)
     *
     * @see
     * org.activiti.engine.delegate.TaskListener#notify(org.activiti.engine.delegate
     * .DelegateTask)
     */
    public void notify(DelegateTask delegateTask) {
        Leave leave = leaveService.get(new Long(delegateTask.getExecution().getProcessBusinessKey()));
        Object realityStartTime = delegateTask.getVariable("realityStartTime");
        leave.setRealityStartTime((Date) realityStartTime);
        Object realityEndTime = delegateTask.getVariable("realityEndTime");
        leave.setRealityEndTime((Date) realityEndTime);
        leaveService.update(leave);
    }

}

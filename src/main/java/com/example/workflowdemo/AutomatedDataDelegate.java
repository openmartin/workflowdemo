package com.example.workflowdemo;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;

import java.util.Date;

public class AutomatedDataDelegate implements JavaDelegate {


    public void execute(DelegateExecution execution) throws Exception {
        Date now = new Date();
        execution.setVariable("autoWelcomeTime", now);
        System.out.println("Faux call to backend for ["
                + execution.getVariable("fullName") + "]");
    }

}

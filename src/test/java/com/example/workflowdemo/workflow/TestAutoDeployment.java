package com.example.workflowdemo.workflow;

import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles(profiles = "test")
@ContextConfiguration("classpath:applicationContext.xml")
public class TestAutoDeployment {

    @Autowired
    RepositoryService repositoryService;

    @Autowired
    RuntimeService runtimeService;


    @Test
    public void testAutoDeployment() {
        long count = repositoryService.createProcessDefinitionQuery().count();
        assertEquals(1, count);
    }

}

package com.example.workflowdemo.workflow;

import org.activiti.engine.IdentityService;
import org.activiti.engine.identity.Group;
import org.activiti.engine.identity.User;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles(profiles = "test")
@ContextConfiguration("classpath:applicationContext.xml")
public class TestSyncUserAndGroup {

    @Autowired
    private IdentityService identityService;

    @Test
    public void testUser() {
        User user =  identityService.newUser("1");
        user.setEmail("user01@example.com");
        identityService.saveUser(user);
    }

    @Test
    public void testGroup() {
        Group group1 = identityService.newGroup("DeptLeader");
        group1.setName("部门领导");
        group1.setType("assignment");
        identityService.saveGroup(group1);

        Group group2 = identityService.newGroup("Hr");
        group2.setName("人力");
        group2.setType("assignment");
        identityService.saveGroup(group2);

        List<Group> groupList = identityService.createGroupQuery().list();
        for (Group group : groupList) {
            System.out.println(group.getName());
        }

        Assert.assertEquals(2, groupList.size());

    }


    @Test
    public void testMembership() {
        User user02 =  identityService.newUser("2");
        user02.setEmail("user02@example.com");
        identityService.saveUser(user02);
        identityService.createMembership("2","DeptLeader");

        User user03 =  identityService.newUser("3");
        user03.setEmail("user03@example.com");
        identityService.saveUser(user03);
        identityService.createMembership("3","Hr");
    }

}

package com.example.workflowdemo.service;

import com.example.workflowdemo.domain.Permission;
import com.example.workflowdemo.domain.Role;
import com.example.workflowdemo.domain.User;
import com.example.workflowdemo.mapper.UserMapper;
import org.apache.shiro.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class UserService {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private UserMapper userMapper;

    public User findByUsername(String username) {
        return userMapper.findByUsername(username);
    }

    public Set<Role> findRoles(String username) {
        return userMapper.findRoles(username);
    }

    public Set<Permission> findPermissions(String username) {
        return userMapper.findPermissions(username);
    }

    public User getLocalUser() {
        String username = (String) SecurityUtils.getSubject().getPrincipal();
        return userMapper.findByUsername(username);
    }
}

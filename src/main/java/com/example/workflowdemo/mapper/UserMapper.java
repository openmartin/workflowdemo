package com.example.workflowdemo.mapper;

import com.example.workflowdemo.domain.Permission;
import com.example.workflowdemo.domain.Role;
import com.example.workflowdemo.domain.User;

import java.util.Set;

public interface UserMapper {

    public User findByUsername(String username);

    public Set<Role> findRoles(String username);

    public Set<Permission> findPermissions(String username);

}

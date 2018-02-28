package com.example.workflowdemo.mapper;

import com.example.workflowdemo.domain.Leave;

import java.util.List;

public interface LeaveMapper {

    public long insert(Leave leave);

    public void update(Leave leave);

    public Leave get(long leaveId);

    public List<Leave> query(Leave leave);

}

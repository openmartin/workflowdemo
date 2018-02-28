package com.example.workflowdemo.service;

import com.example.workflowdemo.domain.Leave;
import com.example.workflowdemo.mapper.LeaveMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class LeaveService {

    @Autowired
    private LeaveMapper leaveMapper;

    public long insert(Leave leave) {
        long rows = leaveMapper.insert(leave);
        return rows;
    }

    public void update(Leave leave) {
        leaveMapper.update(leave);
    }

    public Leave get(long leaveId) {
        return leaveMapper.get(leaveId);
    }

    public List<Leave> query(Leave leave) {
        return leaveMapper.query(leave);
    }


}

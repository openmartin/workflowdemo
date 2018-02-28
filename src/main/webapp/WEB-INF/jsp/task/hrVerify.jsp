<%--
  Created by IntelliJ IDEA.
  User: Pengzili
  Date: 2018/2/7
  Time: 18:10
  To change this template use File | Settings | File Templates.
--%>
<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>hrVerify</title>
</head>
<body>
<c:if test="${not empty error}">
    <div id="error" class="alert alert-error">${error}</div>
</c:if>
<c:if test="${not empty message}">
    <div id="message" class="alert alert-success">${message}</div>
</c:if>
<form action="/task/complete/${task.id}" method="post">
    <input type="hidden" name="id" value="${leave.leaveId}" />
    <fieldset>
        <legend>请假申请</legend>
        <p>
            <label>申请人</label>
            <span>${leave.userId}</span>
        </p>
        <p>
            <label>申请时间</label>
            <span><fmt:formatDate value="${leave.applyTime}" pattern="yyyy-MM-dd hh:mm:SSS"/></span>
        </p>
        <p>
            <label>请假类型</label>
            <span>${leave.leaveType}</span>
        </p>
        <p>
            <label>开始时间</label>
            <span><fmt:formatDate value="${leave.startTime}" pattern="yyyy-MM-dd"/></span>
        </p>
        <p>
            <label>结束时间</label>
            <span><fmt:formatDate value="${leave.endTime}" pattern="yyyy-MM-dd"/></span>
        </p>
        <p>
            <label>请假原因</label>
            <span>${leave.reason}</span>
        </p>
        <p>
            <label>审批意见</label>
            <select name="p_B_hrApproved" id="deptLeaderApproved">
                <option value="true">同意</option>
                <option value="false">拒绝</option>
            </select>
        </p>
        <p>
            <input type="submit" value="完成任务">
        </p>
    </fieldset>
</form>
</body>
</html>

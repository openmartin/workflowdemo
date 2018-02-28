<%--
  Created by IntelliJ IDEA.
  User: Pengzili
  Date: 2018/2/7
  Time: 18:09
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
    <title>taskList</title>
</head>
<body>
<c:if test="${not empty error}">
    <div id="error" class="alert alert-error">${error}</div>
</c:if>
<c:if test="${not empty message}">
    <div id="message" class="alert alert-success">${message}</div>
</c:if>
<table>
    <thead>
    <tr>
        <th>申请人</th>
        <th>类型</th>
        <th>请假时间</th>
        <th>请假原因</th>
        <th>任务ID</th>
        <th>任务名称</th>
        <th>流程实例ID</th>
        <th>流程定义ID</th>
        <th>任务创建时间</th>
        <th>操作</th>
    </tr>
    </thead>
    <tbody>
    <c:forEach items="${records}" var="leave">
        <tr>
            <td>${leave.userId}</td>
            <td>${leave.leaveType}</td>
            <td><fmt:formatDate value="${leave.startTime}" pattern="yyyy-MM-dd"/>至<br/><fmt:formatDate value="${leave.endTime}" pattern="yyyy-MM-dd"/></td>
            <td>${leave.reason}</td>
            <td>${leave.task.id }</td>
            <td>${leave.task.name }</td>
            <td>${leave.task.processInstanceId }</td>
            <td>${leave.task.processDefinitionId }</td>
            <td><fmt:formatDate value="${leave.task.createTime }" pattern="yyyy-MM-dd hh:mm:SSS"/></td>
            <td>
                <c:if test="${empty leave.task.assignee }">
                    <a class="btn" href="/task/claim/${leave.task.id}"><i class="icon-eye-open"></i>签收</a>
                </c:if>
                <c:if test="${not empty leave.task.assignee }">
                    <a class="btn" href="/task/view/${leave.task.id}"><i class="icon-user"></i>办理</a>
                </c:if>
            </td>
        </tr>
    </c:forEach>
    </tbody>
</table>
</body>
</html>

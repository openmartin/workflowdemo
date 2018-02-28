<%--
  Created by IntelliJ IDEA.
  User: Pengzili
  Date: 2018/2/12
  Time: 11:05
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
        <th>执行ID</th>
        <th>流程实例ID</th>
        <th>所属流程</th>
        <th>流程定义ID</th>
        <th>当前节点</th>
        <th>查看流程图</th>
    </tr>
    </thead>
    <tbody>
    <c:forEach items="${page.result }" var="e">
        <tr>
            <td>${e.id}</td>
            <td>${e.processInstanceId}</td>
            <td>${definitions[e.processDefinitionId].name}</td>
            <td>${e.processDefinitionId}</td>
            <td>
                <c:forEach items="${currentActivityMap[e.id]}" var="acid">
                    <c:set var="task" value="${taskMap[acid]}" />
                            <%-- 处理[调用活动] --%>
                        <c:if test="${task.processDefinitionId != e.processDefinitionId}">
                            <span title='引用了外部流程'>${definitions[task.processDefinitionId].name}</span><i style="margin-left: 0.5em;" class="icon-circle-arrow-right"></i>
                        </c:if>
                            ${task.name}
                    <c:if test="${empty task.assignee}">（<span class="text-info">未签收</span>）</c:if>
                    <c:if test="${not empty task.assignee}">
                        （<span class="text-info">办理中</span><i class="icon-user"></i><span class="text-success">${task.assignee}</span>）
                    </c:if>
                </c:forEach>
            </td>
            <td><a href="/task/trace/${e.id}" target="_blank" >查看流程图</a></td>
        </tr>
    </c:forEach>
    </tbody>
</table>
<tags:pagination page="${page}" paginationSize="${page.pageSize}"/>
</body>
</html>

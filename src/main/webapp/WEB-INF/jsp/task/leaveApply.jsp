<%--
  Created by IntelliJ IDEA.
  User: Pengzili
  Date: 2018/2/9
  Time: 10:35
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
    <title>请假申请</title>
</head>
<body>
<c:if test="${not empty error}">
    <div id="error" class="alert alert-error">${error}</div>
</c:if>
<c:if test="${not empty message}">
    <div id="message" class="alert alert-success">${message}</div>
</c:if>
<form action="/task/leave/start" method="post">
    <fieldset>
        <legend>请假申请</legend>
        <p>
            <label>请假类型</label>
            <select id="leaveType" name="leaveType" required="required">
                <option>公休</option>
                <option>病假</option>
                <option>调休</option>
                <option>事假</option>
                <option>婚假</option>
            </select>
        </p>
        <p>
            <label>开始时间</label>
            <input type="date" name="startTime" required="required">
        </p>
        <p>
            <label>结束时间</label>
            <input type="date" name="endTime" required="required">
        </p>
        <p>
            <label>请假原因</label>
            <textarea name="reason"></textarea>
        </p>
        <p>
            <input type="submit" value="启动流程">
        </p>
    </fieldset>
</form>
</body>
</html>

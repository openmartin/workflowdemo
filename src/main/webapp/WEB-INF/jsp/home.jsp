<%--
  Created by IntelliJ IDEA.
  User: Pengzili
  Date: 2018/2/6
  Time: 13:51
  To change this template use File | Settings | File Templates.
--%>
<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Home Page</title>
</head>
<body>
<h1> Home </h1>
<shiro:guest>
    <h2>游客</h2>
</shiro:guest>
<shiro:user>
    <h2><shiro:principal></shiro:principal> <a href="/logout">退出</a></h2>
    <h3><a href="/task/leave/apply">请假申请</a></h3>
    <h3><a href="/task/list">待办事项</a></h3>
    <h3><a href="/task/involved">参与的流程(未结束的)</a></h3>
    <h3><a href="/task/history">已经结束的流程</a></h3>
</shiro:user>
</body>
</html>

<%--
  Created by IntelliJ IDEA.
  User: Pengzili
  Date: 2018/1/24
  Time: 15:01
  To change this template use File | Settings | File Templates.
--%>
<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Login Form</title>
</head>
<body>
<c:if test="${not empty error}">
    <div id="error" class="alert alert-error">${error}</div>
</c:if>
<c:if test="${not empty message}">
    <div id="message" class="alert alert-success">${message}</div>
</c:if>
<form method="post">
    <fieldset>
        <legend>Login</legend>
        <p>
            <label>username</label>
            <input type="text" name="username">
        </p>
        <p>
            <label>password</label>
            <input type="password" name="password">
        </p>
        <p>
            <label>language</label>
            <select name="lang">
                <option value="zh_CN">中文</option>
                <option value="en_US">EN</option>
            </select>
        </p>
        <p>
            <input type="submit" value="Submit">
        </p>
    </fieldset>
</form>

</body>
</html>

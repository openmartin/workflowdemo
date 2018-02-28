<%--
  Created by IntelliJ IDEA.
  User: Pengzili
  Date: 2018/2/12
  Time: 15:13
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>流程图</title>
</head>
<script type="text/javascript" src="/resources/js/jquery.js"></script>
<script type="text/javascript">
    var processInstanceId = '${historicProcessInstance.processInstanceId}';
    var executionId = '${executionId}';
</script>
<script type="text/javascript" src="/resources/js/trace-process.js"></script>
<body>
<div>
    <img id="processDiagram" src="/task/trace/data/auto/" + executionId>
</div>
</body>
</html>

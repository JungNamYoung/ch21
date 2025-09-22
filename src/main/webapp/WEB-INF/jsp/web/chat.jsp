
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<!DOCTYPE html>

<html>
<head>
<meta charset="UTF-8">
<title>user</title>

<c:set var="cp" value="${pageContext.request.contextPath eq '/' ? '' : pageContext.request.contextPath}" />

<link href="${cp}/haru/bundle/bootstrap.video/bootstrap.css" rel="stylesheet" type="text/css" />
<link rel="stylesheet" href="${cp}/haru/css/user.css">

</head>
<body>
  <div class="container mt-3">
    <div class="d-flex flex-column align-items-center gap-4 ">

      <h1 class="d-flex align-items-center">
        <img class="img-fluid" style="width: 100px; height: auto; margin-right: 10px;" src="${cp}/haru/images/haru-logo.png"> <span> web-container</span>
      </h1>

      <div class="w-50"></div>
    </div>
  </div>
</body>

</html>
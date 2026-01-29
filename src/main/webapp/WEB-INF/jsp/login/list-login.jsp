<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<c:set var="cp" value="${pageContext.request.contextPath eq '/' ? '' : pageContext.request.contextPath}" />

<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1" />
<title>login</title>

<link href="${cp}/haru/bundle/bootstrap.video/bootstrap.css" rel="stylesheet" type="text/css" />
<link rel="stylesheet" href="${cp}/haru/css/user.css">

</head>
<body>
  <div class="container mt-3">
    <div class="d-flex flex-column align-items-center gap-4 ">

      <h1 class="d-flex align-items-center">
        <img class="img-fluid" style="width: 100px; height: auto; margin-right: 10px;" src="${cp}/haru/images/haru-logo.png"> <span> framework</span>
      </h1>

      <div class="w-50">
        <table class="table table-bordered text-center">
          <thead class="table-light">
            <tr>
              <th>userId</th>
              <th>userName</th>
              <th>desc</th>
            </tr>
          </thead>
          <tbody>
            <c:forEach var="result" items="${results}">
              <tr>
                <td>${result.userId}</td>
                <td>${result.userName}</td>
                <td>${result.description}</td>
              </tr>
            </c:forEach>
          </tbody>
        </table>

      </div>
      <div class="mt-3">
        <button type="button" class="btn btn-primary" onclick="location.href='${cp}/change-login.do'">수정화면으로 이동</button>
      </div>
    </div>
  </div>
</body>

</html>
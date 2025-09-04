
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<!DOCTYPE html>

<html>
<head>
<meta charset="UTF-8">
<title>user</title>

<link href="<c:url value="/haru/bundle/bootstrap.video/bootstrap.css"/>" rel="stylesheet" type="text/css" />
<link rel="stylesheet" href="<c:url value='/haru/css/user.css'/>">

</head>
<body>
  <div class="container mt-3">
    <div class="d-flex flex-column align-items-center gap-4 ">

      <h1 class="d-flex align-items-center">
        <img class="img-fluid" style="width: 100px; height: auto; margin-right: 10px;" src="<c:url value='/haru/images/haru-logo.png'/>"> 
        <span> web-container</span>
      </h1>
      <h5>author : ${author}</h5>

      <img class="img-fluid w-50" src="<c:url value='/haru/images/Countryside1.png'/>">
      <div class="table-responsive w-50">
        <table class="table table-bordered text-center">
          <thead class="table-light">
            <tr>
              <th>userId</th>
              <th>userName</th>
              <th>description</th>
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
      <div class="d-flex gap-3">
        <button class="btn btn-outline-primary" id="id-insert-user">insert user</button>
        <button class="btn btn-outline-primary" id="id-insert-update-user">insert/update user</button>
      </div>
      <div class="w-50">
        <video controls class="img-fluid">
          <source src="<c:url value='/haru/video/output.mp4'/>" />
        </video>
      </div>
    </div>
  </div>
</body>

<script>
document.getElementById('id-insert-update-user').addEventListener('click',function(){
  fetch('<c:url value="/changeUser.do"/>', {
    method: 'POST',
    headers : {
      'Content-Type': 'application/json'
    }
  })
  .then (response => {
    if(!response.ok) {
      throw new Error('네트워크 오류');
    } 
    return response.text();
  })
  .then (result => { 
    console.log("변경 결과: ", result);
    alert("사용자 변경 결과: " + result);
    window.location.reload(true);
  })
  .catch(error => {
    console.error("요청 실패:", error);
    alert("요청 중 오류 발생");
  });
});
</script>


</html>
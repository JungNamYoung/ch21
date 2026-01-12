<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>로그인 정보 변경</title>

<c:set var="cp" value="${pageContext.request.contextPath eq '/' ? '' : pageContext.request.contextPath}"/>

<script type="text/javascript">
	function fn_submit() {
		var frm = document.changeForm;

		//간단한 유효성 검사(예:ID 필수 입력)
		if (frm.colId.value == "") {
			alert("ID를 입력해주세요.");
			frm.colId.focus();
			return false;
		}

		frm.submit();
	}
</script>
</head>
<body>

  <h2>사용자 정보 수정</h2>
  <form name="changeForm" action="${cp}/login/change-login.do" method="post">
    <table>
      <tr>
        <th>일련번호(SN)</th>
        <td><input type="text" name="colSn" value="1"></td>
      </tr>
      <tr>
        <th>아이디(ID)</th>
        <td><input type="text" name="colId" value="kim"></td>
      </tr>
      <tr>
        <th>이름</th>
        <td><input type="text" name="colName" value="min"></td>
      </tr>
      <tr>
        <th>설명</th>
        <td><textarea name="colDescription"></textarea></td>
      </tr>
      <tr>
        <th>사용여부</th>
        <td><select name="colUseYn">
            <option value="Y">사용</option>
            <option value="N">미사용</option>
        </select></td>
      </tr>
      <tr>
        <th>등록자</th>
        <td><input type="text" name="colRegUser" value=""></td>
      </tr>
    </table>
    <br>
    <button type="button" onclick="fn_submit();">정보 변경 저장</button>
  </form>


</body>
</html>
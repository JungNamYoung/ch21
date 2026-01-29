<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<c:set var="cp" value="${pageContext.request.contextPath eq '/' ? '' : pageContext.request.contextPath}" />

<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1" />
<title>로그인 정보 변경</title>
<link href="${cp}/haru/bundle/bootstrap.video/bootstrap.css" rel="stylesheet" type="text/css" />
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
  <div class="container mt-5">
    <h2 class="mb-4">사용자 정보 수정</h2>
    <form name="changeForm" action="${cp}/change-login.do" method="post">
      <div class="mb-3 row">
        <label class="col-sm-2 col-form-label fw-bold">일련번호(SN)</label>
        <div class="col-sm-10">
          <input type="text" name="colSn" class="form-control" value="1" readonly>
        </div>
      </div>

      <div class="mb-3 row">
        <label class="col-sm-2 col-form-label fw-bold">아이디(ID)</label>
        <div class="col-sm-10">
          <input type="text" name="colId" class="form-control" value="kim">
        </div>
      </div>
      <div class="mb-3 row">
        <label class="col-sm-2 col-form-label fw-bold">이름</label>
        <div class="col-sm-10">
          <input type="text" name="colName" class="form-control" value="min">
        </div>
      </div>
      <div class="mb-3 row">
        <label class="col-sm-2 col-form-label fw-bold">설명</label>
        <div class="col-sm-10">
          <textarea name="colDescription" class="form-control" rows="3"></textarea>
        </div>
      </div>
      <div class="mb-3 row">
        <label class="col-sm-2 col-form-label fw-bold">사용여부</label>
        <div class="col-sm-10">
          <select name="colUseYn" class="form-select">
            <option value="Y" selected>사용</option>
            <option value="N">미사용</option>
          </select>
        </div>
      </div>

      <div class="mb-3 row">
        <label class="col-sm-2 col-form-label fw-bold">등록자</label>
        <div class="col-sm-10">
          <input type="text" name="colRegUser" class="form-control" value="">
        </div>
      </div>
      <hr class="my-4">
      <div class="d-flex justify-content-end gap-2">
        <button type="button" class="btn btn-outline-secondary" onclick="history.back()">뒤로</button>
        <button type="button" class="btn btn-primary" onclick="fn_submit();">정보 변경 저장</button>
      </div>
    </form>
  </div>
</body>
</html>
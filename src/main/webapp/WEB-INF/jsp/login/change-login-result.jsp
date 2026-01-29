<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<c:set var="cp" value="${pageContext.request.contextPath eq '/' ? '' : pageContext.request.contextPath}" />

<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8" />
<meta name="viewport" content="width=device-width, initial-scale=1" />
<title>사용자 정보 저장 결과</title>
<link href="${cp}/haru/bundle/bootstrap.video/bootstrap.css" rel="stylesheet" type="text/css" />
</head>
<body class="bg-light">
  <div class="container py-5" style="max-width: 920px;">
    <div class="mb-3">
      <h2 class="fw-bold mb-1">사용자 수정 결과</h2>
      <div class="text-muted">입력한 사용자 정보가 저장 처리되었습니다.</div>
    </div>

    <div class="alert alert-success d-flex align-items-center" role="alert">
      <div class="me-2">v</div>
      <div>${message}</div>
    </div>

    <div class="card shadow-sm mb-4">
      <div class="card-header bg-white">
        <span class="fw-semibold">저장된 사용자 정보</span>
      </div>
      <div class="card-body">
        <div class="table-responsive">
          <table class="table table-bordered align-middle mb-0">
            <tbody>
              <tr>
                <th class="bg-light" style="width: 180px;">일련번호(SN)</th>
                <td>${userCol.colSn}</td>
              </tr>
              <tr>
                <th class="bg-light">아이디(ID)</th>
                <td>${userCol.colId}</td>
              </tr>
              <tr>
                <th class="bg-light">이름</th>
                <td>${userCol.colName}</td>
              </tr>
              <tr>
                <th class="bg-light">설명</th>
                <td><c:out value="${userCol.colDescription}" /></td>
              </tr>
              <tr>
                <th class="bg-light">사용여부</th>
                <td><span class="badge text-bg-${userCol.colUseYn == 'Y' ? 'success' : 'secondary'}"> ${userCol.colUseYn} </span></td>
              </tr>
              <tr>
                <th class="bg-light">등록자</th>
                <td>${userCol.colRegUser}</td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>

    <div class="d-flex gap-2">
      <button type="button" class="btn btn-outline-secondary" onclick="history.back()">뒤로</button>
      <button type="button" class="btn btn-primary" onclick="location.href='${cp}/list-login.do'">목록으로 이동</button>
    </div>
  </div>
</body>
</html>